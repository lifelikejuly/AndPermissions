package com.july.studio.permissions.example

import android.app.Application
import android.content.Context

/**
 * @author JulyYu
 * @date 2023/12/20.
 * description：
 */
class App : Application() {


    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}