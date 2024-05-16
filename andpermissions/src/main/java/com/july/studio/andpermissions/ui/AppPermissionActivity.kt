package com.july.studio.andpermissions.ui

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.july.studio.andpermissions.RequestPermissionHandler

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 */
class AppPermissionActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_CONFIG = "permission_config"
        const val FROM_CONTENT = "from_content"
    }

    lateinit var appPermissionFragment: AppPermissionFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        appPermissionFragment = AppPermissionFragment()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, appPermissionFragment)
            .commitNowAllowingStateLoss()
        intent.apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                getParcelableExtra(PERMISSION_CONFIG,PermissionConfig::class.java)?.apply {
//                    appPermissionFragment.requestPermissions(this)
//                }
//            }else{
            getBooleanExtra(FROM_CONTENT, false).apply {
                appPermissionFragment.isFromContent(this)
            }
            getParcelableExtra<RequestPermissionHandler>(PERMISSION_CONFIG)?.apply {
                appPermissionFragment.requestPermissions(this)
            }

//            }
        }
    }


    override fun onResume() {
        super.onResume()
        overridePendingTransition(0,0)
    }

}