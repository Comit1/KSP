package com.comit.ksp.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo

/*
 * Created by Comit on 2022/8/27.
 */
class ServiceGenerator {

    companion object {

        private const val PACKAGE_NAME = "com.comit.service"

        private const val CLASS_NAME = "ServiceProvidersImpl"

    }

    @OptIn(KotlinPoetKspPreview::class)
    fun generate(
        codeGenerator: CodeGenerator,
        logger: KSPLogger,
        classList: List<KSClassDeclaration>
    ) {

        // 添加文件
        val fileSpecBuilder = FileSpec.builder(PACKAGE_NAME, CLASS_NAME)

        val classBuilder = TypeSpec.Companion.classBuilder(CLASS_NAME)

        val map = ClassName("kotlin.collections", "Map")
        val arrayList = ClassName("kotlin.collections", "HashMap")
        val service = ClassName("com.comit.service", "IService")
        val mapOfServices = map.parameterizedBy(STRING, service)
        val hashMapServices = arrayList.parameterizedBy(STRING, service)

        val funcSpecBuilder = FunSpec.builder("getServices")
            .returns(mapOfServices)
            .addStatement("val result = %T()", hashMapServices)

        for (clazz in classList) {
            val packageName = "${clazz.packageName.getQualifier()}.${clazz.packageName.getShortName()}"
            val className = ClassName(packageName, clazz.simpleName.getShortName())
            val key = getSuperName(clazz)
            if (key.isNullOrEmpty()) {
                continue
            }
            funcSpecBuilder.addStatement("result[%S] = %T()", key, className)
        }

        funcSpecBuilder.addStatement("return result")
        classBuilder.addFunction(funcSpecBuilder.build())

        fileSpecBuilder
            .addType(classBuilder.build())
            .build()
            .writeTo(codeGenerator, false)

    }

    private fun getSuperName(clazz: KSClassDeclaration): String? {
        val superType = clazz.superTypes.first().resolve().declaration as? KSClassDeclaration
        return superType?.qualifiedName?.asString()
    }

}