package com.july.studio.andpermissions.permission

import android.Manifest


/**
 * 权限组
 */
object PermissionGroups {

    private val storagePermissionGroups = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private val calendarPermissionGroups = listOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
    )

    private val contactsPermissionGroups = listOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS,
    )

    private val bluetoothPermissionGroups = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE,
    )


    fun collectPermissionGroup(permissions: List<String>): List<ArrayList<String>> {
        var maps = mutableMapOf<String,ArrayList<String>>()
        for (permission in permissions){
            if(storagePermissionGroups.contains(permission)){
                addPermission(maps,"storage",permission)
                continue
            }
            if(calendarPermissionGroups.contains(permission)){
                addPermission(maps,"calendar",permission)
                continue
            }
            if(contactsPermissionGroups.contains(permission)){
                addPermission(maps,"contacts",permission)
                continue
            }
            if(bluetoothPermissionGroups.contains(permission)){
                addPermission(maps,"bluetooth",permission)
                continue
            }
            addPermission(maps,permission,permission)
        }
        return maps.map {
            it.value
        }.toList()
    }

    private fun addPermission(maps: MutableMap<String,ArrayList<String>>,key:String,permission: String){
        if(maps[key] == null){
            maps[key] = arrayListOf()
        }
        maps[key]?.add(permission)
    }
}