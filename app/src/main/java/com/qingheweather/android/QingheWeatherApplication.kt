package com.qingheweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class QingheWeatherApplication : Application() {

    companion object {

        const val TOKEN = "bqFWdTlgAoC8XmgI" // 填入你申请到的令牌值

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}