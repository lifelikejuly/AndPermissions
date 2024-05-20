package com.july.studio.andpermissions.ui

import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.july.studio.andpermissions.RequestPermissionHandler
import com.july.studio.andpermissions.permission.ResultHandler

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：旧版本Fragment
 */
class AppPermissionOldFragment : Fragment() {

    private val resultCode = 999
    private var isRequest = false
    private var requestPermissionHandler: RequestPermissionHandler? = null
    private var permissionHandler: ResultHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionHandler?.apply {
            requestPermissions(this)
        }
    }

    private fun finish() {
        if (activity is AppPermissionActivity) {
            (activity as AppPermissionActivity).finish()
        }
    }

    fun requestPermissions(config: RequestPermissionHandler) {
        this.requestPermissionHandler = config
        if (activity != null && !isRequest) {
            isRequest = true
            requestPermissionHandler?.apply {
                permissionHandler = config.getCallbackWrapper()
//                permissionHandler?.onRationaleCallback?.apply {
//                    var rationaleResults: MutableMap<String, Boolean> = mutableMapOf()
//                    var isRationaleAllPass = true
//                    for (permission in permissions!!) {
//                        var boolResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            shouldShowRequestPermissionRationale(permission)
//                        } else {
//                            true
//                        }
//                        if (boolResult) isRationaleAllPass = false
//                        rationaleResults[permission] = boolResult
//                    }
//                    if (!isRationaleAllPass) {
//                        onRationaleResult(rationaleResults)
//                        finish()
//                        return
//                    }
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions!!.toTypedArray(), resultCode)
                }
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == resultCode) {
            var permissionResults: MutableMap<String, Boolean> = mutableMapOf()
            var isAllGranted = true
            for (permission in permissions) {
                val result =  grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED
                permissionResults[permission] = result
                isAllGranted = isAllGranted && result
            }
            permissionHandler?.onPermissionResult(isAllGranted,permissionResults)
            finish()
        }
    }
}