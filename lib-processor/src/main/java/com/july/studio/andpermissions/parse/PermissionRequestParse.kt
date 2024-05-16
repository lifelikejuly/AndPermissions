package com.july.studio.andpermissions.parse

import com.july.studio.andpermissions.CodeBuildConsts
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

/**
 * @author JulyYu
 * @date 2023/12/25.
 * description：
 */
class PermissionRequestParse {

    fun createKotlinObject(
        requestPermissionMaps: HashMap<ClassName, Array<String>>,
        filer: javax.annotation.processing.Filer
    ) {
        // 初始化方法
        val initFun = CodeBlock.builder()
        initFun.apply {
            requestPermissionMaps.forEach { (className, permissions) ->
                val clas = className.simpleName
                val strings = permissions.joinTo(StringBuilder()) { "\"$it\"" }
                addStatement("map[$clas::class.java] = listOf(%L)", strings)
            }
        }
        // 创建map对象
        val pageMap = HashMap::class.asClassName().parameterizedBy(
            Class::class.asClassName().parameterizedBy(STAR),
            List::class.parameterizedBy(String::class)
        )
        // 查询方法
        val findFun = FunSpec.builder(CodeBuildConsts.MethodInspect)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("clazz",Class::class.asClassName().parameterizedBy(STAR))
            .addStatement("return  map[clazz] ?: listOf()")
            .returns( List::class.parameterizedBy(String::class))
        val file = FileSpec.builder(CodeBuildConsts.PackageName, CodeBuildConsts.ClazzName)
            .addType(
                TypeSpec.classBuilder( CodeBuildConsts.ClazzName)
                    .addProperty(
                        PropertySpec.builder("map", pageMap, KModifier.PRIVATE)
                            .initializer("HashMap<Class<*>, List<String>>()")
                            .build()
                    )
                    .addSuperinterface(ClassName(CodeBuildConsts.PackagePortName, CodeBuildConsts.InterfaceName))
                    .addInitializerBlock(initFun.build())
                    .addFunction(
                        findFun.build()
                    )
                    .build()
            )
        requestPermissionMaps.keys.forEach { className ->
            file.addImport(className.packageName, className.simpleName)
        }
        file.build().writeTo(filer)
    }
}