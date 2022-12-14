package com.comit.ksp.compiler

import com.comit.ksp.annotation.Provider
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

/*
 * Created by Comit on 2022/8/27.
 */
class ServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    companion object {

        private const val KEY_MODULE_NAME = "module_name"

        private const val SERVICE_NAME = "com.comit.service.IService"

    }

    private var isHandleProcess = false

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (isHandleProcess) {
            return emptyList()
        }

        val moduleName = options[KEY_MODULE_NAME]
        if (moduleName.isNullOrEmpty()) {
            logger.error("module name is not correct.")
        }

        val symbols = resolver.getSymbolsWithAnnotation(Provider::class.qualifiedName!!)
        val providerList = symbols
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .toList()

        for (provider in providerList) {
            if (!isValidService(provider)) {
                logger.error("use @Provider annotation error.")
            }
        }

        if (providerList.isNotEmpty()) {
            ServiceGenerator().generate(codeGenerator, logger, moduleName!!, providerList)
        }

        isHandleProcess = true

        return emptyList()
    }

    private fun isValidService(declaration: KSClassDeclaration): Boolean {
        if (declaration.classKind != ClassKind.CLASS) {
            return false
        }
        val constructor = declaration.primaryConstructor
        if (constructor == null || constructor.parameters.isNotEmpty()) {
            return false
        }

        val superType = declaration.superTypes.first().resolve().declaration
        if (superType !is KSClassDeclaration || superType.classKind != ClassKind.INTERFACE) {
            return false
        }

        val serviceType = superType.superTypes.first().resolve().declaration
        if (serviceType !is KSClassDeclaration) {
            return false
        }

        if (SERVICE_NAME != serviceType.qualifiedName?.asString()) {
            return false
        }

        return true
    }

    override fun finish() {
        super.finish()
        isHandleProcess = false
    }

    override fun onError() {
        super.onError()
        isHandleProcess = false
    }

}