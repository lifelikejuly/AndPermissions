package com.july.studio.permissions.example

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.july.studio.andpermissions.annoation.RequestPermissions

/**
 * @author JulyYu
 * @date 2023/12/21.
 * description：
 */

@RequestPermissions(permissions = [
//    Manifest.permission_group.CALENDAR
    Manifest.permission.WRITE_CALENDAR,
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.ACCESS_FINE_LOCATION
])
class AcAnnotationPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun agreePermission(){

    }

    fun refuse(){

    }

}