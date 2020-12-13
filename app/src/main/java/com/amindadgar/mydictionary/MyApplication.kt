package com.amindadgar.mydictionary

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.amindadgar.mydictionary.Utils.FloatingWindow
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple


class MyApplication :Application() {

    private lateinit var floatingWindow:FloatingWindow
    // initialize variable with single data
    private var wordsData : ArrayList<WordDefinitionTuple> = arrayListOf(WordDefinitionTuple(0,"word","definition"))

    override fun onCreate() {
        super.onCreate()

        if (this.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + this.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(intent)
        }



    }


}