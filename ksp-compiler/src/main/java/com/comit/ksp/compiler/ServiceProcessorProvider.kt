package com.comit.ksp.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/*
 * Created by Comit on 2022/8/27.
 */
class ServiceProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ServiceProcessor(environment.codeGenerator, environment.logger)
    }
}