package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.expressions.IrCall

class KotlinMpIrTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    override fun visitCall(expression: IrCall): org.jetbrains.kotlin.ir.expressions.IrExpression {
        val functionName = expression.symbol.owner.name.asString()
        
        if (functionName == "parallelFor") {
            // Report our discovery!
            messageCollector.report(
                CompilerMessageSeverity.WARNING, 
                "-> BOOM! Found our parallelFor block in the IR!"
            )
        }

        return super.visitCall(expression) 
    }
}