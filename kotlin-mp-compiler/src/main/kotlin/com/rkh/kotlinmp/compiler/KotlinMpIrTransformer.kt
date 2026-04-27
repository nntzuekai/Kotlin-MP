package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

class KotlinMpIrTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    override fun visitCall(expression: IrCall): IrExpression {
        val transformedExpression = super.visitCall(expression)
        if (transformedExpression !is IrCall) {
            return transformedExpression
        }

        // Extract the full path: com.rkh.kotlinmp.OmpContext.parallelFor
        val fullFunctionName = transformedExpression.symbol.owner.kotlinFqName.asString()

        return when (fullFunctionName) {
            "com.rkh.kotlinmp.OmpContext.parallelFor" -> lowerParallelFor(transformedExpression)
            "com.rkh.kotlinmp.OmpContext.parallel" -> lowerParallel(transformedExpression)
            else -> transformedExpression
        }
    }

    private fun lowerParallelFor(expression: IrCall): IrExpression {
        // Look at the first parameter of the function the user called
        val param0Type = expression.symbol.owner.valueParameters[0].type.classFqName?.asString()
        val loopType = if (param0Type == "kotlin.ranges.IntRange") "Range" else "Progression"

        val trampolineSuffix: String


        var optimizeDynamicOne = false

        if (expression.valueArgumentsCount == 2) {
            trampolineSuffix = "Static" // Default Overload
        }
        else {
            // 3 arguments

            // 1. ENFORCE INLINE SCHEDULE (Reject `val s = Schedule.Static()`)
            val scheduleArg = expression.getValueArgument(1)!!
            if (scheduleArg is IrGetValue) {
                messageCollector.report(
                    CompilerMessageSeverity.ERROR,
                    "OpenMP Error: Schedule must be defined inline. " +
                            "Use parallelFor(..., Schedule.Static(LITERAL)), do not pass a variable."
                )
                return expression
            }

            // 2. ENFORCE INLINE CHUNK SIZE CONSTANT (Reject `Schedule.Static(n)`)
            // If the schedule is an IrCall (meaning they used the invoke operator)
            // AND it has arguments (meaning they typed a chunk size instead of empty parentheses)

            val actualValue: Int?
            if (scheduleArg is IrCall && scheduleArg.valueArgumentsCount > 0) {

                // Grab what they passed as the chunk size
                val chunkSizeArg = scheduleArg.getValueArgument(0)!!

                actualValue = extractConstInt(chunkSizeArg)

                // If it returns null, it means it was a normal `val`, a function call, or something else illegal.
                if (actualValue == null) {
                    messageCollector.report(
                        CompilerMessageSeverity.ERROR,
                        "OpenMP Error: chunkSize must be an inline integer or a 'const val'. " +
                                "Standard runtime variables are not allowed."
                    )
                    return expression
                }

                // 3. ENFORCE CHUNK SIZE >= 1
                if (actualValue <= 0) {
                    messageCollector.report(
                        CompilerMessageSeverity.ERROR,
                        "OpenMP Error: chunkSize must be >= 1. You provided: $actualValue"
                    )
                    return expression
                }
            }
            else{
                actualValue = null
            }

            // 4. MAP TO TRAMPOLINE
            val param1Type = expression.symbol.owner.valueParameters[1].type.classFqName?.asString()
            trampolineSuffix = when (param1Type) {
                "com.rkh.kotlinmp.Schedule.Static" -> "Static"
                "com.rkh.kotlinmp.Schedule.StaticChunked" -> "StaticChunked"
                "com.rkh.kotlinmp.Schedule.Dynamic" -> "DynamicDefault"
                "com.rkh.kotlinmp.Schedule.DynamicChunked" -> {
                    // THE INTERCEPT: If it's DynamicChunked AND the size is exactly 1
                    if (actualValue == 1) {
                        optimizeDynamicOne = true
                        messageCollector.report(
                            CompilerMessageSeverity.INFO,
                            "Optimizing Schedule.Dynamic(1) -> Schedule.Dynamic()"
                        )
                        "DynamicDefault" // Reroute to the faster trampoline!
                    } else {
                        "DynamicChunked" // Keep it normal
                    }
                }
                else -> {
                    messageCollector.report(CompilerMessageSeverity.ERROR, "Unknown schedule type: $param1Type")
                    return expression
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
            return expression
        }

        // 2. Extract the arguments the user passed to parallelFor
        // Index 0: The IntRange (e.g., 0 until 20)
        // Index 1: The Schedule (e.g., Schedule.Dynamic) - We will ignore this for the static prototype
        // Index 2: The Lambda Block (e.g., { i -> process(i) })
//        val rangeArgument = expression.getValueArgument(0)
//        val scheduleArgument = expression.getValueArgument(1)
//        val blockArgument = expression.getValueArgument(2)

        // 3. Create the new AST Node (IrCall) pointing to our support function
        val newCall = IrCallImpl(
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
            type = expression.type,
            symbol = supportFunctionSymbol,
            typeArgumentsCount = expression.typeArgumentsCount,
            valueArgumentsCount = 3, // range, block
            origin = expression.origin
        )

        // 4. Glue the user's extracted arguments into the new call
        if (expression.valueArgumentsCount == 2) {
            // 1. Look up the Schedule.Static class in the compiler's classpath
            val staticScheduleClassId = ClassId.topLevel(FqName("com.rkh.kotlinmp.Schedule.Static"))
            val staticScheduleClassSymbol = pluginContext.referenceClass(staticScheduleClassId)
                ?: error("Compiler Panic: Cannot find com.rkh.kotlinmp.Schedule.Static")

            // 2. Synthesize the AST node that represents reading this singleton object
            val synthesizedStaticSchedule = IrGetObjectValueImpl(
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
                type = staticScheduleClassSymbol.defaultType,
                symbol = staticScheduleClassSymbol
            )

            newCall.putValueArgument(0, expression.getValueArgument(0)) // Slot 0: Range/Progression
            newCall.putValueArgument(1, synthesizedStaticSchedule)      // Slot 1: INJECTED Schedule.Static
            newCall.putValueArgument(2, expression.getValueArgument(1)) // Slot 2: The Lambda Block
        }
        else {
            newCall.putValueArgument(0, expression.getValueArgument(0))

            if (optimizeDynamicOne) {
                val dynamicClassId = ClassId.topLevel(FqName("com.rkh.kotlinmp.Schedule.Dynamic"))
                val dynamicSymbol = pluginContext.referenceClass(dynamicClassId)
                    ?: error("Compiler Panic: Cannot find com.rkh.kotlinmp.Schedule.Dynamic")
                val synthesizedDynamicSchedule = IrGetObjectValueImpl(
                    startOffset = expression.startOffset,
                    endOffset = expression.endOffset,
                    type = dynamicSymbol.defaultType,
                    symbol = dynamicSymbol
                )

                newCall.putValueArgument(1, synthesizedDynamicSchedule) // Inject the optimized singleton
            } else {
                newCall.putValueArgument(1, expression.getValueArgument(1))
            }

            newCall.putValueArgument(2, expression.getValueArgument(2))
        }
        // 5. Return the new call.
        // The compiler completely deletes the old 'parallelFor' and inserts this instead!
        return newCall
    }

    private fun lowerParallel(expression: IrCall): IrExpression {
        val blockArgument = expression.getValueArgument(expression.valueArgumentsCount - 1)
        val usesBarrier = containsBarrierCall(blockArgument)
        val targetTrampolineName = if (usesBarrier) {
            "executeParallelRegionWithBarrier"
        } else {
            "executeParallelRegionWithoutBarrier"
        }

        messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "-> Routing parallel region to $targetTrampolineName"
        )

        val callableId = CallableId(
            packageName = FqName("com.rkh.kotlinmp"),
            callableName = Name.identifier(targetTrampolineName)
        )

        val supportFunctionSymbol = pluginContext.referenceFunctions(callableId).singleOrNull()
        if (supportFunctionSymbol == null) {
            messageCollector.report(CompilerMessageSeverity.ERROR, "Could not find $targetTrampolineName!")
            return expression
        }

        val newCall = IrCallImpl(
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
            type = expression.type,
            symbol = supportFunctionSymbol,
            typeArgumentsCount = expression.typeArgumentsCount,
            valueArgumentsCount = 2,
            origin = expression.origin
        )


        if(expression.valueArgumentsCount == 1){
            newCall.putValueArgument(0, blockArgument)
        }
        else{
            newCall.putValueArgument(0, expression.getValueArgument(0))
            newCall.putValueArgument(1, blockArgument)
        }
        return newCall
    }

    private fun containsBarrierCall(expression: IrExpression?): Boolean {
        if (expression == null) return false

        var foundBarrier = false
        expression.acceptChildrenVoid(object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                if (!foundBarrier) {
                    element.acceptChildrenVoid(this)
                }
            }

            override fun visitCall(expression: IrCall) {
                if (expression.symbol.owner.kotlinFqName.asString() == "com.rkh.kotlinmp.ParallelScope.barrier") {
                    foundBarrier = true
                    return
                }
                visitElement(expression)
            }

            override fun visitFunctionExpression(expression: IrFunctionExpression) {
                visitElement(expression)
            }
        })
        return foundBarrier
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
