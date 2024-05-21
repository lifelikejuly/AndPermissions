package com.july.studio.andpermissions.permission

import android.content.Context
import com.july.studio.andpermissions.AuthorLauncher
import com.july.studio.andpermissions.callback.OnResultCallback

abstract class ResultHandler(
    private var context: Context,
    private var onResultCallback: OnResultCallback?,
    var requestPermissions: List<String>,
    var authorizedPermissions: List<String>,
): OnResultCallback {

    companion object {
        private var callbackKeyCacheMap: MutableMap<String, ResultHandler> =
            mutableMapOf()

        fun findKeyCallback(keyId: String): ResultHandler? {
            return callbackKeyCacheMap[keyId]
        }

        fun putKeyCallback(keyId: String, permissionHandler: ResultHandler) {
            callbackKeyCacheMap[keyId] = permissionHandler
        }

        fun finishKeyCallback(keyId: String) {
            callbackKeyCacheMap.remove(keyId)
        }
    }



    var keyId: String = "${this.hashCode()}_${System.currentTimeMillis()}"

    init {
        putKeyCallback(keyId,this)
    }

    abstract fun permissions(): List<String>

    override fun onPermissionResult(
        isAllGranted: Boolean,
        permissionResults: MutableMap<String, Boolean>
    ) {
        AuthorLauncher.removeOver(context = context)
        onResultCallback?.onPermissionResult(
            isAllGranted = isAllGranted,
            permissionResults = supplementPermissions(permissionResults)
        )
        finishKeyCallback(keyId)
    }

    private fun supplementPermissions(permissionResults: MutableMap<String, Boolean>): MutableMap<String, Boolean> {
        for (permission in authorizedPermissions) {
            if (!permissionResults.containsKey(permission)) {
                permissionResults[permission] = true
            }
        }
        return permissionResults
    }
}