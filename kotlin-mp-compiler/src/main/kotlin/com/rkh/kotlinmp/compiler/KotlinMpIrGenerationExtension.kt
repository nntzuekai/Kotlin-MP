package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class KotlinMpIrGenerationExtension(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {
    
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        // Print a WARNING so it shows up bright yellow in the Gradle console!
        messageCollector.report(CompilerMessageSeverity.WARNING, "========== KOTLIN-MP COMPILER PLUGIN EXECUTING ==========")
        
        val transformer = KotlinMpIrTransformer(pluginContext, messageCollector)
        moduleFragment.transform(transformer, null)
    }
}