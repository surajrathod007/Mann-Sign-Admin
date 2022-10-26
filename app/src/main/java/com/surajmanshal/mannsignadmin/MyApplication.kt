package com.surajmanshal.mannsignadmin

import android.app.Application
import com.onesignal.OneSignal

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

    }

    companion object{
        private const val ONESIGNAL_APP_ID = "b82fc38c-ab9f-425e-b3b3-75531ccace29"
    }
}