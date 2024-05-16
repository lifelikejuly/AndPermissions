package com.july.studio.andpermissions.port

/**
 * @author JulyYu
 * @date 2024/1/4.
 * description：
 */
interface PermissionCollectorPort {

    fun inspect(clazz: Class<*>): List<String>
}