package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.ir.util.kotlinFqName

class KotlinMpIrTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    override fun visitCall(expression: IrCall): IrExpression {
        // Extract the full path: com.rkh.kotlinmp.OmpContext.parallelFor
        val fullFunctionName = expression.symbol.owner.kotlinFqName.asString()

        // Strictly check the fully qualified name
        if (fullFunctionName == "com.rkh.kotlinmp.OmpContext.parallelFor") {

            // Look at the first parameter of the function the user called
            val param0Type = expression.symbol.owner.valueParameters[0].type.classFqName?.asString()
            val loopType = if (param0Type == "kotlin.ranges.IntRange") "Range" else "Progression"

            val trampolineSuffix: String

            if (expression.valueArgumentsCount == 2) {
                trampolineSuffix = "Static" // Default Overload
            }
            else {
                // 1. ENFORCE INLINE SCHEDULE (Reject `val s = Schedule.Static()`)
                val scheduleArg = expression.getValueArgument(1)!!
                if (scheduleArg is IrGetValue) {
                    messageCollector.report(
                        CompilerMessageSeverity.ERROR,
                        "OpenMP Error: Schedule must be defined inline. " +
                                "Use parallelFor(..., Schedule.Static(LITERAL)), do not pass a variable."
                    )
                    return super.visitCall(expression)
                }

                // 2. ENFORCE INLINE CHUNK SIZE CONSTANT (Reject `Schedule.Static(n)`)
                // If the schedule is an IrCall (meaning they used the invoke operator)
                // AND it has arguments (meaning they typed a chunk size instead of empty parentheses)
                if (scheduleArg is IrCall && scheduleArg.valueArgumentsCount > 0) {

                    // Grab what they passed as the chunk size
                    val chunkSizeArg = scheduleArg.getValueArgument(0)!!

                    val actualValue = extractConstInt(chunkSizeArg)

                    // If it returns null, it means it was a normal `val`, a function call, or something else illegal.
                    if (actualValue == null) {
                        messageCollector.report(
                            CompilerMessageSeverity.ERROR,
                            "OpenMP Error: chunkSize must be an inline integer or a 'const val'. " +
                                    "Standard runtime variables are not allowed."
                        )
                        return super.visitCall(expression)
                    }

                    // 3. ENFORCE CHUNK SIZE >= 1
                    if (actualValue <= 0) {
                        messageCollector.report(
                            CompilerMessageSeverity.ERROR,
                            "OpenMP Error: chunkSize must be >= 1. You provided: $actualValue"
                        )
                        return super.visitCall(expression)
                    }
                }

                // 4. MAP TO TRAMPOLINE
                val param1Type = expression.symbol.owner.valueParameters[1].type.classFqName?.asString()
                trampolineSuffix = when (param1Type) {
                    "com.rkh.kotlinmp.Schedule.Static" -> "Static"
                    "com.rkh.kotlinmp.Schedule.StaticChunked" -> "StaticChunked"
                    "com.rkh.kotlinmp.Schedule.Dynamic" -> "DynamicDefault"
                    "com.rkh.kotlinmp.Schedule.DynamicChunked" -> "DynamicChunked"
                    else -> {
                        messageCollector.report(CompilerMessageSeverity.ERROR, "Unknown schedule type: $param1Type")
                        return super.visitCall(expression)
                    }
                }
            }

            val targetTrampolineName = "executeParallel${loopType}${trampolineSuffix}"

            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "-> Routing to highly-optimized $targetTrampolineName"
            )

            // 1. Find our hidden Trampoline function using CallableId (The modern 1.9+ way)
            val callableId = CallableId(
                packageName = FqName("com.rkh.kotlinmp"),
                callableName = Name.identifier(targetTrampolineName)
            )

            val supportFunctionSymbol = pluginContext.referenceFunctions(callableId).singleOrNull()

            if (supportFunctionSymbol == null) {
                messageCollector.report(CompilerMessageSeverity.ERROR, "Could not find $targetTrampolineName!")
                return super.visitCall(expression)
            }

            // 2. Extract the arguments the user passed to parallelFor
            // Index 0: The IntRange (e.g., 0 until 20)
            // Index 1: The Schedule (e.g., Schedule.Dynamic) - We will ignore this for the static prototype
            // Index 2: The Lambda Block (e.g., { i -> process(i) })
            val rangeArgument = expression.getValueArgument(0)
            val scheduleArgument = expression.getValueArgument(1)
            val blockArgument = expression.getValueArgument(2)

            // 3. Create the new AST Node (IrCall) pointing to our support function
            val newCall = IrCallImpl(
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
                type = expression.type,
                symbol = supportFunctionSymbol,
                typeArgumentsCount = 0,
                valueArgumentsCount = 3 // range, block
            )

            // 4. Glue the user's extracted arguments into the new call
            newCall.putValueArgument(0, rangeArgument)
            newCall.putValueArgument(1, scheduleArgument)
            newCall.putValueArgument(2, blockArgument)

            // 5. Return the new call.
            // The compiler completely deletes the old 'parallelFor' and inserts this instead!
            return newCall
        }

        return super.visitCall(expression)
    }

    private fun extractConstInt(expression: IrExpression?): Int? {
        if (expression == null) return null

        return when (expression) {
            // Case 1: Hardcoded number (e.g., Schedule.Static(2))
            is IrConst<*> -> expression.value as? Int

            // Case 2: Direct field access (Sometimes happens in the same class)
            is IrGetField -> {
                val property = expression.symbol.owner.correspondingPropertySymbol?.owner
                if (property?.isConst == true) {
                    val initializer = property.backingField?.initializer?.expression
                    (initializer as? IrConst<*>)?.value as? Int
                } else null
            }

            // Case 3: Property Getter Call
            is IrCall -> {
                val function = expression.symbol.owner

                // 1. Is this function call actually a getter for a property?
                val property = function.correspondingPropertySymbol?.owner

                // 2. Is that property strictly marked as a `const val`?
                if (property?.isConst == true) {
                    // 3. Dig into the backing field and grab the actual integer!
                    val initializer = property.backingField?.initializer?.expression
                    (initializer as? IrConst<*>)?.value as? Int
                } else null
            }

            else -> null
        }
    }
}