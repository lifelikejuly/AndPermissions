package com.july.studio.andpermissions.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.july.studio.andpermissions.RequestPermissionHandler
import com.july.studio.andpermissions.callback.PermissionCallbackWrapper

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 */
class AppPermissionFragment : Fragment() {

    private var isRequest = false
    private var isFromContent = false // 是否来自内部的
    private var requestPermissionHandler: RequestPermissionHandler? = null
    private var permissionCallback: PermissionCallbackWrapper? = null
    private val result_code = 999



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

    fun isFromContent(isFrom: Boolean) {
        isFromContent = isFrom
    }
    fun requestPermissions(config: RequestPermissionHandler) {
        this.requestPermissionHandler = config
        if (host != null && !isRequest) {
            isRequest = true
            requestPermissionHandler?.apply {
                permissionCallback = config.getCallbackWrapper()
                if(permissionCallback?.onRationaleCallback != null){
                    permissionCallback!!.onRationaleCallback!!.apply {
                        val rationaleResults: MutableMap<String, Boolean> = mutableMapOf()
                        var isRationaleAllPass = true
                        for (permission in permissions!!) {
                            val boolResult = shouldShowRequestPermissionRationale(permission)
                            if (boolResult) isRationaleAllPass = false
                            rationaleResults[permission] = boolResult
                        }
                        if (!isRationaleAllPass) {
                            onRationaleResult(rationaleResults)
                            if(isFromContent) {
                                finish()
                            }
                            return
                        }
                        requestPermissions(permissions!!.toTypedArray(), result_code)
                    }
                }else{
                    requestPermissions(permissions!!.toTypedArray(), result_code)
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
        if (requestCode == result_code) {
            var permissionResults: MutableMap<String, Boolean> = mutableMapOf()
            var isAllGranted = true
            for (permission in permissions) {
                val value =
                    grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED
                permissionResults[permission] = value
                isAllGranted = isAllGranted && value
            }
            permissionCallback?.onPermissionResult(isAllGranted,permissionResults)
            if(isFromContent){
                finish()
            }
        }
    }
}