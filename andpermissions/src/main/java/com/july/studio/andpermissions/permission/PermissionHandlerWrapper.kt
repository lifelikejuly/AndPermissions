package com.july.studio.andpermissions.permission

import android.content.Context
import com.july.studio.andpermissions.AuthorLauncher
import com.july.studio.andpermissions.callback.OnResultCallback
import com.july.studio.andpermissions.callback.OnPermissionRun
import com.july.studio.andpermissions.callback.OnRationaleCallback
import com.july.studio.andpermissions.callback.OnExplainCallback

class PermissionHandlerWrapper(
    private var context: Context,
    private var authorizedPermissions: List<String>,
    private var onExplainCallback: OnExplainCallback? = null,
    private var onResultCallback: OnResultCallback? = null,
    requestPermissions: List<String>,
    onRationaleCallback: OnRationaleCallback? = null,
    explainEach: Boolean = false
    ) {

    private var permissionHandler: ResultHandler
    private var permissionHandlers = mutableListOf<ResultHandler>()
    private var permissionResultsMap: MutableMap<String, Boolean> = mutableMapOf()

    init {
        val permissionGroupsCopy = if(explainEach){
            var permissionGroups = PermissionGroups.collectPermissionGroup(requestPermissions)
            permissionGroups.toMutableList()
        }else{
            mutableListOf(requestPermissions.toMutableList())
        }
        while (permissionGroupsCopy.isNotEmpty()) {
            var permissions = permissionGroupsCopy.removeFirst()
            permissionHandlers.add(ResultMultipleHandler(
                context= context,
                requestPermissions = permissions,
                authorizedPermissions = authorizedPermissions,
                onResultCallback = object : OnResultCallback {
                    override fun onPermissionResult(
                        isAllGranted: Boolean,
                        permissionResults: MutableMap<String, Boolean>
                    ) {
                        permissionResultsMap.putAll(permissionResults)
                        forLaunchHandler()
                    }
                },
                onRationaleCallback = object : OnRationaleCallback {
                    override fun onRationaleResult(rationaleResults: MutableMap<String, Boolean>) {
                        permissionResultsMap.putAll(rationaleResults)
                        forLaunchHandler()
                    }
                }
            ))
        }
        permissionHandler = permissionHandlers.removeFirst()
    }

    // 循环申请单个判断权限
    private fun forLaunchHandler() {
        if (permissionHandlers.size > 0) {
            permissionHandler = permissionHandlers.removeFirst()
            launch()
        } else {
            if (onResultCallback != null) {
                val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
                for (permission in authorizedPermissions) {
                    permissionResults[permission] = true
                }
                var isAllGranted = true
                for (permission in permissionResultsMap) {
                    isAllGranted = isAllGranted && permission.value
                }
                permissionResultsMap.putAll(permissionResultsMap)
                onResultCallback!!.onPermissionResult(isAllGranted,permissionResultsMap)
            }
        }
    }

    fun launch() {
        if (onExplainCallback != null) {
            onExplainCallback!!.onExplain(
                permissionHandler.permissions(),
                onRun = object : OnPermissionRun {
                    override fun onRun() {
                        AuthorLauncher.authorLaunch(context, permissionHandler)
                    }

                    override fun onCancel() {
                        for (permission in permissionHandler.permissions()){
                            permissionResultsMap[permission] = false
                        }
                        forLaunchHandler()
                    }
                })
        } else {
            permissionHandler.apply {
                AuthorLauncher.authorLaunch(context, this)
            }
        }
    }
}