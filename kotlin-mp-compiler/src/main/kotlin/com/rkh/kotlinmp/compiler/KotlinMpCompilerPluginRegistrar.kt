package com.rkh.kotlinmp.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KotlinMpCompilerPluginRegistrar : CompilerPluginRegistrar() {
    
    override val supportsK2: Boolean = false 

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        // Grab the official compiler message collector
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        // Pass it to our extension
        IrGenerationExtension.registerExtension(KotlinMpIrGenerationExtension(messageCollector))
    }
}