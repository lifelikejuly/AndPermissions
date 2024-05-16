package com.july.studio.andpermissions.callback

interface OnPermissionCallback {

    fun onPermissionRequesting(permissions: List<String>)
    fun onPermissionResult(isAllGranted: Boolean, permissionResults: MutableMap<String, Boolean>)
}