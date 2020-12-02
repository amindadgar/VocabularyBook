package com.amindadgar.mydictionary

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.amindadgar.mydictionary.Utils.FloatingWindow


class Application :Application() {
    private lateinit var floatingWindow:FloatingWindow
    override fun onCreate() {
        super.onCreate()

        if (this.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + this.getPackageName())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(intent)
        }
        Log.d("ApplicationClass", "onCreate: YES")
        floatingWindow = FloatingWindow(this)
        floatingWindow.open()
    }

}