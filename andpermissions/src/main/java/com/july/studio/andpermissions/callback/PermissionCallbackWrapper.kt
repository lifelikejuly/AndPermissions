package com.july.studio.andpermissions.callback

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.july.studio.andpermissions.AuthorLauncher
import com.july.studio.andpermissions.ui.AppPermissionActivity
import java.util.ArrayList


/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 */
class PermissionCallbackWrapper(
    private var context: Context,
    private var onPermissionCallback: OnPermissionCallback?,
    var onRationaleCallback: OnRationaleCallback?,
    var permissions: List<String>,
    var authorizedPermissions: List<String>,
    var keyId: String? = null,
    ) :
    OnPermissionCallback, OnRationaleCallback {


    companion object {
        private var callbackKeyCacheMap: MutableMap<String, PermissionCallbackWrapper> =
            mutableMapOf()

        fun findKeyCallback(keyId: String): PermissionCallbackWrapper? {
            return callbackKeyCacheMap[keyId]
        }

        fun putKeyCallback(keyId: String, permissionInterfaceWrapper: PermissionCallbackWrapper) {
            callbackKeyCacheMap[keyId] = permissionInterfaceWrapper
        }

        fun finishKeyCallback(keyId: String) {
            callbackKeyCacheMap.remove(keyId)
        }
    }

    init {
        keyId = "${this.hashCode()}_${System.currentTimeMillis()}"
        putKeyCallback(keyId!!, this)
    }


    override fun onPermissionRequesting(permissions: List<String>) {
        onPermissionCallback?.onPermissionRequesting(permissions = permissions)
    }


    override fun onPermissionResult(
        isAllGranted: Boolean,
        permissionResults: MutableMap<String, Boolean>
    ) {
        onPermissionCallback?.onPermissionResult(
            isAllGranted = isAllGranted,
            permissionResults = supplementPermissions(permissionResults)
        )
        releaseAll()
    }

    override fun onRationaleResult(rationaleResults: MutableMap<String, Boolean>) {
        onRationaleCallback?.onRationaleResult(
            rationaleResults = supplementPermissions(
                rationaleResults
            )
        )
    }

    private fun supplementPermissions(permissionResults: MutableMap<String, Boolean>): MutableMap<String, Boolean> {
        for (permission in authorizedPermissions) {
            if (!permissionResults.containsKey(permission)) {
                permissionResults[permission] = true
            }
        }
        return permissionResults
    }

    private fun releaseAll() {
        finishKeyCallback(keyId!!)
        context?.apply {
            if (this is FragmentActivity) {
                AuthorLauncher.removeFragment(this)
                if (this is AppPermissionActivity) {
                    finish()
                }
            } else if (this is Activity) {
                AuthorLauncher.removeOldFragment(this)
            }
        }
    }

}



