package com.july.studio.permissions.example

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.july.studio.andpermissions.AndPermissions
import com.july.studio.andpermissions.callback.OnPermissionCallback

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

    }


    class PageFragment : Fragment() {
        private fun request(context: Context) {
            var checker =
                AndPermissions.Builder(context = context)
                    .permissions(
                        listOf(
//                            Manifest.permission_group.CALENDAR
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR,
                        )
                    )
                    .onPermissionCallback(object : OnPermissionCallback {
                        override fun onPermissionRequesting(permissions: List<String>) {
//                            Toast.makeText(
//                                context,
//                                "onPermissionRequesting ${permissions}",
//                                Toast.LENGTH_LONG
//                            ).show()
                        }
                        override fun onPermissionResult(
                            isAllGranted: Boolean,
                            permissionResults: MutableMap<String, Boolean>
                        ) {
                            if (isAllGranted) {
//                                Toast.makeText(
//                                    context,
//                                    "authorized ${permissionResults}",
//                                    Toast.LENGTH_LONG
//                                ).show()
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
                        var permissions =
                            AndPermissions.check(
                                context, listOf(
                                    Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.READ_CALENDAR,
                                )
                            )
                        permissions.forEach {
                            Log.v("<><>", "permissionResults: ${it.key} ${it.value}")
                        }
                    }
                })
                addView(Button(context).apply {
                    text = "跳转页面前先检查权限"
                    setOnClickListener {
                        AndPermissions.jumpActivityCheck(
                            context,
                            AcAnnotationPage::class.java,
                            onPermissionCallback = object :
                                OnPermissionCallback {
                                override fun onPermissionRequesting(permissions: List<String>) {
//                                    Toast.makeText(
//                                        context,
//                                        "onPermissionRequesting ${permissions}",
//                                        Toast.LENGTH_LONG
//                                    ).show()

                                }

                                override fun onPermissionResult(
                                    isAllGranted: Boolean,
                                    permissionResults: MutableMap<String, Boolean>
                                ) {
                                    permissionResults.forEach {
                                        Log.v("<><>", "permissionResults: ${it.key} ${it.value}")
                                    }

                                    if (isAllGranted) {
//                                        Toast.makeText(
//                                            context,
//                                            "authorized ${permissionResults}",
//                                            Toast.LENGTH_LONG
//                                        ).show()
                                        Intent(context, AcAnnotationPage::class.java).apply {
                                            startActivity(this)
                                        }
                                    }
//                                    Toast.makeText(
//                                        context,
//                                        "onPermissionRequesting ${permissionResults.keys}",
//                                        Toast.LENGTH_LONG
//                                    ).show()
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