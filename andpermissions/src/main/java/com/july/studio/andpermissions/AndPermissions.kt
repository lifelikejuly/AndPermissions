package com.july.studio.andpermissions

import android.content.Context
import androidx.core.content.PermissionChecker
import com.july.studio.andpermissions.callback.OnResultCallback
import com.july.studio.andpermissions.callback.OnRationaleCallback
import com.july.studio.andpermissions.callback.OnExplainCallback
import com.july.studio.andpermissions.permission.PermissionHandlerWrapper
import com.july.studio.andpermissions.port.PermissionCollectorPort
import java.lang.ref.SoftReference

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 * https://blog.csdn.net/yeluofengchui/article/details/91126163
 * https://github.com/githubZYQ/easypermission
 * 权限工具
 */
class AndPermissions private constructor(builder: Builder) {

    private var context: SoftReference<Context>
    private var permissions: List<String> = emptyList()
    private var onResultCallback: OnResultCallback? = null
    private var onRationaleCallback: OnRationaleCallback? = null
    private var onExplainCallback: OnExplainCallback? = null
    private var explainEach: Boolean = false


    companion object {
        private var permissionCollector: PermissionCollectorPort? = null

        /**
         * 检查是否需要权限
         */
        fun check(context: Context, permissions: List<String>): Map<String, Boolean> {
            val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
            for (permission in permissions) {
                permissionResults[permission] = PermissionChecker.checkSelfPermission(
                    context,
                    permission
                ) == PermissionChecker.PERMISSION_GRANTED
            }
            return permissionResults
        }

        /**
         * 跳转前检查是否需要权限支持
         */
        fun jumpActivityCheck(
            context: Context, clazz: Class<*>,
            onResultCallback: OnResultCallback? = null,
            onRationaleCallback: OnRationaleCallback? = null
        ) {
            if (findNeedPermissions(clazz)) {
                val permissions = permissionCollector!!.inspect(clazz)
                if (check(context, permissions).values.contains(false)) {
                    val checker = Builder(context = context)
                        .permissions(
                            permissions
                        )
                        .onPermissionCallback(onResultCallback)
                        .onRationaleCallback(onRationaleCallback)
                        .build()
                    checker.request()
                } else {
                    val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
                    for (permission in permissions) {
                        permissionResults[permission] = true
                    }
                    onResultCallback?.onPermissionResult(true, permissionResults)
                }
            } else {
                val permissions = permissionCollector?.inspect(clazz) ?: emptyList()
                val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
                for (permission in permissions) {
                    permissionResults[permission] = true
                }
                onResultCallback?.onPermissionResult(true, permissionResults)
            }
        }

        /**
         * 判断页面是否需要权限申请
         */
        private fun findNeedPermissions(clazz: Class<*>): Boolean {
            reflectionPermissionCollector()
            return permissionCollector?.inspect(clazz)?.isNotEmpty() ?: false
        }

        /**
         * 反射获取所有需要权限申请的页面
         */
        private fun reflectionPermissionCollector() {
            if (permissionCollector == null) {
                val clazzSet =
                    Class.forName("${CodeBuildConsts.PackageName}.${CodeBuildConsts.ClazzName}")
                val clazzObject = clazzSet.newInstance()
                if (clazzObject is PermissionCollectorPort) {
                    permissionCollector = clazzObject
                }
            }
        }

        private fun collectNeedAuthorizedPermissions(checkResult: Map<String, Boolean>): MutableList<String> {
            val requestPermissions = mutableListOf<String>()
            for ((key, value) in checkResult) {
                if (!value) {
                    requestPermissions.add(key)
                }
            }
            return requestPermissions
        }

        private fun collectAuthorizedPermissions(checkResult: Map<String, Boolean>): MutableList<String> {
            val requestPermissions = mutableListOf<String>()
            for ((key, value) in checkResult) {
                if (value) {
                    requestPermissions.add(key)
                }
            }
            return requestPermissions
        }
    }


    init {
        this.context = builder.context
        this.permissions = builder.permissions
        this.onResultCallback = builder.permissionCallback
        this.onRationaleCallback = builder.rationaleCallback
        this.onExplainCallback = builder.explainCallback
        this.explainEach = builder.explainEach
    }


    private fun launch(
        context: Context,
        requestPermissions: List<String>,
        authorizedPermissions: List<String>
    ) {

        PermissionHandlerWrapper(
            context = context,
            requestPermissions = requestPermissions,
            authorizedPermissions = authorizedPermissions,
            onResultCallback = onResultCallback,
            onRationaleCallback = onRationaleCallback,
            onExplainCallback = onExplainCallback,
            explainEach = explainEach
        ).launch()
    }


    fun request() {
        val softContext = context.get()
        softContext?.apply {
            val checkResult = check(context = this, permissions = permissions)
            val needAuthorizationPermissions = collectNeedAuthorizedPermissions(checkResult)
            val authorizedPermissions = collectAuthorizedPermissions(checkResult)
            if (needAuthorizationPermissions.size > 0) {
                launch(
                    context = this,
                    requestPermissions = needAuthorizationPermissions,
                    authorizedPermissions = authorizedPermissions
                )
            } else {
                val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
                for (permission in permissions) {
                    permissionResults[permission] = true
                }
                onResultCallback?.onPermissionResult(true, permissionResults)
            }
        }

    }


    class Builder(context: Context) {
        internal var permissionCallback: OnResultCallback? = null
        internal var rationaleCallback: OnRationaleCallback? = null
        internal var explainCallback: OnExplainCallback? = null
        internal var context: SoftReference<Context>
        internal var permissions: List<String> = emptyList()
        internal var explainEach: Boolean = false
        init {
            this.context = SoftReference(context)
        }


        fun permissions(permissions: List<String>) = apply { this.permissions = permissions }
        fun onPermissionCallback(permissionCallback: OnResultCallback?) = apply {
            this.permissionCallback = permissionCallback
        }

        fun onRationaleCallback(rationaleCallback: OnRationaleCallback?) = apply {
            this.rationaleCallback = rationaleCallback
        }

        fun onExplainPermission(explainCallback: OnExplainCallback?) = apply {
            this.explainCallback = explainCallback
        }

        fun explainEachGroup(value: Boolean)=apply {
            this.explainEach = value
        }

        fun build() = AndPermissions(this)
    }

}