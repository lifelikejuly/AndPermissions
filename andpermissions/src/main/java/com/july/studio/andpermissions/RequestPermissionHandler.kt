package com.july.studio.andpermissions

import android.os.Parcel
import android.os.Parcelable
import com.july.studio.andpermissions.permission.ResultHandler
import java.util.ArrayList

/**
 * @author JulyYu
 * @date 2023/8/11.
 * description：
 */
data class RequestPermissionHandler(
    var permissions: ArrayList<String>?,
    var keyId: String? = "",
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(permissions)
        parcel.writeString(keyId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RequestPermissionHandler> {
        override fun createFromParcel(parcel: Parcel): RequestPermissionHandler {
            return RequestPermissionHandler(parcel)
        }

        override fun newArray(size: Int): Array<RequestPermissionHandler?> {
            return arrayOfNulls(size)
        }


    }


    fun getCallbackWrapper(): ResultHandler? {
        keyId?.apply {
            return ResultHandler.findKeyCallback(keyId = this)
        }
        return null
    }

}