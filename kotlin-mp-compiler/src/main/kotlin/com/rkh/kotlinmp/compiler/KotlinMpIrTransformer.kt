package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
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
            val firstParamType = expression.symbol.owner.valueParameters[0].type.classFqName?.asString()

            // Decide which trampoline to use based on the parameter type!
            val targetTrampolineName = if (firstParamType == "kotlin.ranges.IntRange") {
                "executeParallelRangeStatic"
            } else {
                "executeParallelProgressionStatic"
            }

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
            val blockArgument = expression.getValueArgument(2)

            // 3. Create the new AST Node (IrCall) pointing to our support function
            val newCall = IrCallImpl(
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
                type = expression.type,
                symbol = supportFunctionSymbol,
                typeArgumentsCount = 0,
                valueArgumentsCount = 2 // range, block
            )

            // 4. Glue the user's extracted arguments into the new call
            newCall.putValueArgument(0, rangeArgument)
            newCall.putValueArgument(1, blockArgument)

            // 5. Return the new call.
            // The compiler completely deletes the old 'parallelFor' and inserts this instead!
            return newCall
        }

        return super.visitCall(expression)
    }
}