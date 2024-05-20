package com.july.studio.andpermissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.july.studio.andpermissions.permission.ResultHandler
import com.july.studio.andpermissions.ui.AppPermissionActivity
import com.july.studio.andpermissions.ui.AppPermissionFragment
import com.july.studio.andpermissions.ui.AppPermissionOldFragment

/**
 * @author JulyYu
 * @date 2023/12/21.
 * description：
 */
internal object AuthorLauncher {


    fun authorLaunch(context: Context, permissionHandler: ResultHandler) {
        when (context) {
            is FragmentActivity -> {
                launchWithFragment(context, permissionHandler)
            }

            is ComponentActivity -> {
                if (!context.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    launchWithActivity(context, permissionHandler)
                } else {
                    launchWithPermissionActivity(context, permissionHandler, true)
                }
            }

            is Activity -> {
                launchWithOldFragment(context, permissionHandler)
            }

            else -> {
                launchWithPermissionActivity(
                    context = context,
                    handler = permissionHandler,
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
        handler: ResultHandler,
        isContent: Boolean = false
    ) {
        Intent(context, AppPermissionActivity::class.java).apply {
            putExtra(
                AppPermissionActivity.FROM_CONTENT, isContent
            )
            putExtra(
                AppPermissionActivity.PERMISSION_CONFIG, RequestPermissionHandler(
                    keyId = handler.keyId,
                    permissions = ArrayList(handler.requestPermissions)
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
        activity: ComponentActivity, handler: ResultHandler
    ) {
        RequestPermissionHandler(
            keyId = handler.keyId,
            permissions = ArrayList(handler.requestPermissions),
        ).apply {
            val permissionCallback = getCallbackWrapper()
//            permissionCallback?.onRationaleCallback?.apply {
//                val rationaleResults: MutableMap<String, Boolean> = mutableMapOf()
//                var isRationaleAllPass = true
//                for (permission in handler.requestPermissions) {
//                    val boolResult =
//                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
//                    if (boolResult) isRationaleAllPass = false
//                    rationaleResults[permission] = boolResult
//                }
//                if (!isRationaleAllPass) {
//                    onRationaleResult(rationaleResults)
//                    return
//                }
//            }
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
        activity: FragmentActivity, handler: ResultHandler
    ) {
        val appPermissionFragment = AppPermissionFragment()
        activity.supportFragmentManager.beginTransaction()
            .add(appPermissionFragment,"permission-fragment").commitNowAllowingStateLoss()
        appPermissionFragment.requestPermissions(
            config = RequestPermissionHandler(
                keyId = handler.keyId,
                permissions = ArrayList(handler.requestPermissions),
            )
        )
    }

    /**
     * 兼容旧版本的Activity中添加Fragment形式申请权限
     */
    private fun launchWithOldFragment(
        activity: Activity, handler: ResultHandler,
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
                keyId = handler.keyId,
                permissions = ArrayList(handler.requestPermissions),
            )
        )
    }



    fun removeOver(context: Context){
        when (context) {
            is Activity -> {
                removeOldFragment(context)
            }

           is FragmentActivity -> {
                removeFragment(context)
            }
        }
    }
    private fun removeOldFragment(activity: Activity) {
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

    private fun removeFragment(activity: FragmentActivity) {
        val appPermissionFragment =
            activity.supportFragmentManager.findFragmentByTag("permission-fragment")
        if (appPermissionFragment is AppPermissionFragment) {
            activity.supportFragmentManager.beginTransaction().remove(appPermissionFragment)
                .commitNowAllowingStateLoss()
        }
    }
}