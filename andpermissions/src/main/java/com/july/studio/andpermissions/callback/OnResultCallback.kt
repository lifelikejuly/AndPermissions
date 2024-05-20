package com.july.studio.andpermissions.callback

interface OnResultCallback {
    fun onPermissionResult(isAllGranted: Boolean, permissionResults: MutableMap<String, Boolean>)
}