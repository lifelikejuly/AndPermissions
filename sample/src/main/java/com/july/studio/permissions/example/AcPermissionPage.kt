package com.july.studio.permissions.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.july.studio.andpermissions.AndPermissions
import com.july.studio.andpermissions.callback.OnResultCallback
import com.july.studio.andpermissions.callback.OnPermissionRun
import com.july.studio.andpermissions.callback.OnExplainCallback
import java.util.function.Consumer

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 */
class AcPermissionPage : AppCompatActivity() {

    private lateinit var launcher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var page = PageFragment()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, page)
            .show(page)
            .commit()
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        }
//        shouldShowRequestPermissionRationale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            packageManager.getGroupOfPlatformPermission(
                Manifest.permission.WRITE_CALENDAR,
                ContextCompat.getMainExecutor(this@AcPermissionPage),
                object : Consumer<String> {
                    override fun accept(t: String) {
                        Log.d("<><>", "accept ---> $t")
                    }

                })
        }
//        packageManager.getPlatformPermissionsForGroup()
    }


    class PageFragment : Fragment() {
        private fun request(context: Context) {
            var checker =
                AndPermissions.Builder(context = context)
                    .permissions(
                        listOf(
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.CAMERA,
                        )
                    )
                    .explainEachGroup(false)
                    .onExplainPermission(object : OnExplainCallback {

                        override fun onExplain(permissions: List<String>, onRun: OnPermissionRun) {
                            var dialog = AlertDialog.Builder(requireActivity())
                                .setMessage("为什么需要${permissions}权限说明")
                                .setNegativeButton(
                                    "取消"
                                ) { dialog, _ ->
                                    onRun.onCancel()
                                    dialog.dismiss()
                                }
                                .setPositiveButton("可以") { dialog, _ ->
                                    onRun.onRun()
                                    dialog.dismiss()
                                }
                            dialog.setOnDismissListener {
                                onRun.onCancel()
                            }
                            dialog.show()
                        }

                    })
                    .onPermissionCallback(object : OnResultCallback {
                        override fun onPermissionResult(
                            isAllGranted: Boolean,
                            permissionResults: MutableMap<String, Boolean>
                        ) {
                            if (isAllGranted) {
                                Toast.makeText(
                                    context,
                                    "authorized ${permissionResults}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            permissionResults.forEach {
                                Log.v("<><>", "permissionResults: ${it.key} ${it.value}")
                            }
                        }

                    })
                    .build()
            checker.request()
        }


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            var view = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(Button(context).apply {
                    text = "只检查是否有权限"
                    setOnClickListener {
                        val code = PermissionChecker.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_CALENDAR
                        )
                        var rationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.WRITE_CALENDAR)
                        var rationale2 = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)
                        var request = PermissionChecker.checkSelfPermission(requireActivity(),Manifest.permission.WRITE_CALENDAR)
                        Log.v(
                            "<><>",
                            "code $code rationale $rationale  rationale2 $rationale2 request $request"
                        )
                        var permissions =
                            AndPermissions.check(
                                context, listOf(
                                    Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.READ_CALENDAR,
                                )
                            )
                        permissions.forEach {
                            Log.v(
                                "<><>",
                                " permissionResults: ${it.key} ${it.value}"
                            )
                        }
                    }
                })
                addView(Button(context).apply {
                    text = "跳转页面前先检查权限"
                    setOnClickListener {
                        AndPermissions.jumpActivityCheck(
                            context,
                            AcAnnotationPage::class.java,
                            onResultCallback = object :
                                OnResultCallback {

                                override fun onPermissionResult(
                                    isAllGranted: Boolean,
                                    permissionResults: MutableMap<String, Boolean>
                                ) {
                                    permissionResults.forEach {
                                        Log.v("<><>", "permissionResults: ${it.key} ${it.value}")
                                    }

                                    if (isAllGranted) {
                                        Intent(context, AcAnnotationPage::class.java).apply {
                                            startActivity(this)
                                        }
                                    }
                                }
                            })
                    }
                })
//                addView(Button(context).apply {
//                    text = "页面跳转前检查权限"
//                    setOnClickListener {
//                        request(context)
//                    }
//                })
                addView(Button(context).apply {
                    text = "Fragment请求权限"
                    setOnClickListener {
                        request(context)
                    }
                })
                addView(Button(context).apply {
                    text = "fragment的Activity请求权限"
                    setOnClickListener {
                        request(requireActivity())
                    }
                })
                addView(Button(context).apply {
                    text = "全局context请求权限"
                    setOnClickListener {
                        request(App.context)
                    }
                })

            }
            return view
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }
    }
}