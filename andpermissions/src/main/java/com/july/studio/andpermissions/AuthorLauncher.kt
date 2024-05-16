package com.july.studio.andpermissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.july.studio.andpermissions.callback.PermissionCallbackWrapper
import com.july.studio.andpermissions.ui.AppPermissionActivity
import com.july.studio.andpermissions.ui.AppPermissionFragment
import com.july.studio.andpermissions.ui.AppPermissionOldFragment

/**
 * @author JulyYu
 * @date 2023/12/21.
 * description：
 */
internal object AuthorLauncher {


    fun authorLaunch(context: Context, callbackWrapper: PermissionCallbackWrapper) {
        when (context) {
            is FragmentActivity -> {
                launchWithFragment(context, callbackWrapper)
            }

            is ComponentActivity -> {
                if (!context.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    launchWithActivity(context, callbackWrapper)
                } else {
                    launchWithPermissionActivity(context, callbackWrapper, true)
                }
            }

            is Activity -> {
                launchWithOldFragment(context, callbackWrapper)
            }

            else -> {
                launchWithPermissionActivity(
                    context = context,
                    callbackWrapper = callbackWrapper,
                    isContent = true
                )
            }
        }
    }


    /**
     * 启动内部PermissionActivity申请权限
     */
    private fun launchWithPermissionActivity(
        context: Context,
        callbackWrapper: PermissionCallbackWrapper,
        isContent: Boolean = false
    ) {
        Intent(context, AppPermissionActivity::class.java).apply {
            putExtra(
                AppPermissionActivity.FROM_CONTENT, isContent
            )
            putExtra(
                AppPermissionActivity.PERMISSION_CONFIG, RequestPermissionHandler(
                    keyId = callbackWrapper.keyId,
                    permissions = ArrayList(callbackWrapper.permissions)
                )
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(this)
        }
    }

    /**
     * android架构中的ComponentActivity申请权限
     */
    private fun launchWithActivity(
        activity: ComponentActivity, callbackWrapper: PermissionCallbackWrapper
    ) {
        RequestPermissionHandler(
            keyId = callbackWrapper.keyId,
            permissions = ArrayList(callbackWrapper.permissions),
        ).apply {
            val permissionCallback = getCallbackWrapper()
            permissionCallback?.onRationaleCallback?.apply {
                val rationaleResults: MutableMap<String, Boolean> = mutableMapOf()
                var isRationaleAllPass = true
                for (permission in callbackWrapper.permissions) {
                    val boolResult =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                    if (boolResult) isRationaleAllPass = false
                    rationaleResults[permission] = boolResult
                }
                if (!isRationaleAllPass) {
                    onRationaleResult(rationaleResults)
                    return
                }
            }
            val launcher =
                activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    val permissionResults: MutableMap<String, Boolean> = mutableMapOf()
                    var isAllGranted = true
                    for (permission in it.entries) {
                        permissionResults[permission.key] = permission.value
                        isAllGranted = isAllGranted && permission.value
                    }
                    permissionCallback?.onPermissionResult(isAllGranted, permissionResults)
                }
            launcher.launch(permissions!!.toTypedArray())
        }
    }

    /**
     * Activity中添加Fragment形式申请权限
     */
    private fun launchWithFragment(
        activity: FragmentActivity, callbackWrapper: PermissionCallbackWrapper
    ) {
        val appPermissionFragment = AppPermissionFragment()
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, appPermissionFragment).commitNowAllowingStateLoss()
        appPermissionFragment.requestPermissions(
            config = RequestPermissionHandler(
                keyId = callbackWrapper.keyId,
                permissions = ArrayList(callbackWrapper.permissions),
            )
        )
    }

    /**
     * 兼容旧版本的Activity中添加Fragment形式申请权限
     */
    private fun launchWithOldFragment(
        activity: Activity, callbackWrapper: PermissionCallbackWrapper,
    ) {
        var appPermissionOldFragment = AppPermissionOldFragment()
        var fragmentTransaction = activity.fragmentManager.beginTransaction()
            .add(android.R.id.content, appPermissionOldFragment, "permission-fragment")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentTransaction.commitNowAllowingStateLoss()
        } else {
            fragmentTransaction.commitAllowingStateLoss()
        }
        appPermissionOldFragment.requestPermissions(
            config = RequestPermissionHandler(
                keyId = callbackWrapper.keyId,
                permissions = ArrayList(callbackWrapper.permissions),
            )
        )
    }


    fun removeOldFragment(activity: Activity) {
        var appPermissionOldFragment =
            activity.fragmentManager.findFragmentByTag("permission-fragment")
        if (appPermissionOldFragment is AppPermissionOldFragment) {
            var fragmentTransaction =
                activity.fragmentManager.beginTransaction().remove(appPermissionOldFragment)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fragmentTransaction.commitNowAllowingStateLoss()
            } else {
                fragmentTransaction.commitAllowingStateLoss()
            }
        }
    }

    fun removeFragment(activity: FragmentActivity) {
        val appPermissionFragment =
            activity.supportFragmentManager.findFragmentById(android.R.id.content)
        if (appPermissionFragment is AppPermissionFragment) {
            activity.supportFragmentManager.beginTransaction().remove(appPermissionFragment)
                .commitNowAllowingStateLoss()
        }
    }
}