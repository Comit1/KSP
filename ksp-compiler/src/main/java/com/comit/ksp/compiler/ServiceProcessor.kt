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
    private val logger: KSPLogger
) : SymbolProcessor {

    companion object {

        private const val SERVICE_NAME = "com.comit.service.IService"

    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation(Provider::class.qualifiedName!!)
        val result = symbols.filter { !it.validate() }.toList()
        val providerList = symbols
            .filter { it is KSClassDeclaration && it.validate()  }
            .map { it as KSClassDeclaration }
            .toList()

        for (provider in providerList) {
            if (!isValidService(provider)) {
                logger.error("use @Provider annotation error.")
            }
        }

        if (providerList.isNotEmpty()) {
            ServiceGenerator().generate(codeGenerator, logger, providerList)
        }

        return result
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

}