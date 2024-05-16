package com.july.studio.andpermissions.annoation


/**
 * @author JulyYu
 * @date 2023/12/21.
 * description：
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestPermissions(val permissions: Array<String>)