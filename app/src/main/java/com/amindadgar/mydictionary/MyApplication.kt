package com.amindadgar.mydictionary

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings


class MyApplication :Application() {


    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + this.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(intent)
        }



    }


}