package com.july.studio.andpermissions.permission

/**
 * 特殊权限
 */
object SpecialPermissions {





//    private val permissionGroups = mapOf(
//        "storage" to storagePermissionGroups,
//        "calendar" to calendarPermissionGroups,
//        "contacts" to contactsPermissionGroups,
//        "bluetooth" to bluetoothPermissionGroups
//    )

    // 读取应用列表
    const val GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS"
    // 安装应用
    const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"
    // 悬浮窗功能
    const val SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"
    // vpn功能
    const val BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE"
    // 通知权限
    const val NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE"

}