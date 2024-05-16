package com.july.studio.andpermissions.processor

import com.google.auto.service.AutoService
import com.july.studio.andpermissions.annoation.RequestPermissions
import com.july.studio.andpermissions.parse.PermissionRequestParse
import com.squareup.kotlinpoet.ClassName
import com.sun.source.util.Trees
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


/**
 * @author JulyYu
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class PermissionsProcessor : AbstractProcessor() {
    private lateinit var trees: Trees
    private lateinit var messager: Messager
    private lateinit var filer: javax.annotation.processing.Filer

    private var permissionRequestParse = PermissionRequestParse()
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)

        trees = Trees.instance(processingEnv)
        messager = processingEnvironment.messager
        filer = processingEnvironment.filer
    }


    override fun getSupportedAnnotationTypes(): Set<String> {
        val supportTypes: HashSet<String> = LinkedHashSet()
        supportTypes.add(RequestPermissions::class.java.canonicalName)
        return supportTypes
    }


    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.apply {
            if (!roundEnv.processingOver()) {
                collectRequestPermissions(roundEnv)
            }
        }
        return true
    }

    private fun collectRequestPermissions( roundEnv: RoundEnvironment){
        var requestPermissionMaps = HashMap<ClassName, Array<String>>()
        roundEnv.getElementsAnnotatedWith(RequestPermissions::class.java)
            .forEach {
                //获取包名
                val packageName = it.enclosingElement.toString()
                //获取当前类型的类名
                val classStr = it.simpleName.toString()
                val className = ClassName(packageName, classStr)
                requestPermissionMaps[className] =
                    it.getAnnotation(RequestPermissions::class.java).permissions
            }
        permissionRequestParse.createKotlinObject(requestPermissionMaps,filer)
    }
}