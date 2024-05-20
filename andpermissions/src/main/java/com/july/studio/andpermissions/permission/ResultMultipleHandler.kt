package com.july.studio.andpermissions.permission

import android.content.Context
import com.july.studio.andpermissions.callback.OnResultCallback
import com.july.studio.andpermissions.callback.OnRationaleCallback

class ResultMultipleHandler(
    context: Context,
    requestPermissions: List<String>,
    authorizedPermissions: List<String>,
    onResultCallback: OnResultCallback?,
    onRationaleCallback: OnRationaleCallback?,
) : ResultHandler(
    context = context,
    requestPermissions = requestPermissions,
    authorizedPermissions = authorizedPermissions,
    onResultCallback = onResultCallback,
    onRationaleCallback = onRationaleCallback
) {

    override fun permissions(): List<String> {
       return requestPermissions
    }
}