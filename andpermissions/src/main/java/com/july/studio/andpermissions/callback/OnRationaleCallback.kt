//package com.july.studio.andpermissions.callback
//
///**
//shouldShowRequestPermissionRationale，回到最初的解释“应不应该解释下请求这个权限的目的”。
//1.都没有请求过这个权限，用户不一定会拒绝你，所以你不用解释，故返回false;
//2.请求了但是被拒绝了，此时返回true，意思是你该向用户好好解释下了；
//3.请求权限被禁止了，也不给你弹窗提醒了，所以你也不用解释了，故返回fasle;
//4.请求被允许了，都给你权限了，还解释个啥，故返回false。
//因此调用shouldShowRequestPermissionRationale方法，如果返回true，那么就需要解释下为什么需要这个权限，如果返回false，那么就不用解释了。
//一般情况下官方使用是在请求过权限并被拒绝过则会返回true
// */
//interface OnRationaleCallback {
//    fun onRationaleResult(rationaleResults: MutableMap<String, Boolean>)
//}