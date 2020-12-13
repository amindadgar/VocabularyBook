package com.amindadgar.mydictionary.Utils.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.FloatingWindow
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple

class FloatingService : Service() {
    val INTENT_COMMAND = "com.amindadgar.mydictionary"
    val INTENT_COMMAND_EXIT = "EXIT"
    val INTENT_COMMAND_NOTE = "NOTE"

    private val NOTIFICATION_CHANNEL_GENERAL = "myDictionaryGeneral"
    private val CODE_FOREGROUND_SERVICE = 1
    private val CODE_EXIT_INTENT = 2
    private val CODE_NOTE_INTENT = 3
    private var floatingWindowIsPOpen = false
    private val TAG = "FloatingWindow"

    private val NOTIFICATION_TEXT = "DictionaryFloatingService"

    private var wordsData : ArrayList<WordDefinitionTuple> = arrayListOf(WordDefinitionTuple(0,"word","definition"))
    private lateinit var floatingWindow:FloatingWindow

    /**
     * @param floatingWindowIsPOpen is to get to know floatingWindow was opened or not
     * @param TAG is a Tag for logs
     * @param wordsData is an array to hold our data
     * @param floatingWindow is FloatingWindow class ( used for make floatingWindow )
     */





    override fun onBind(intent: Intent?): IBinder? = null


    /**
     * Remove the foreground notification and stop the service.
     */
    private fun stopService() {
        stopForeground(true)
        stopSelf()
        floatingWindowIsPOpen = false
    }

    private fun setData(wordsData : ArrayList<WordDefinitionTuple>){
        this.wordsData = wordsData
    }

    fun initializeFloatingWindow(wordsData : ArrayList<WordDefinitionTuple>){
        floatingWindow = FloatingWindow(this,wordsData)
        setData(wordsData)
        floatingWindow.refreshData(wordsData)
        if (!floatingWindowIsPOpen) {
            floatingWindow.open()
            floatingWindowIsPOpen = true
        }
        else{
            // the window is open no need to open it again
        }

    }
    /**
     * Create and show the foreground notification.
     */
    private fun showNotification() {


        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val exitIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            this, CODE_EXIT_INTENT, exitIntent, 0
        )

        val notePendingIntent = PendingIntent.getService(
            this, CODE_NOTE_INTENT, noteIntent, 0
        )

        // From Android O, it's necessary to create a notification channel first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL, NOTIFICATION_TEXT, NotificationManager.IMPORTANCE_DEFAULT))
                {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                // Ignore exception.
            }
        }

        with(NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_GENERAL)) {

            setTicker(null)
            setContentTitle(getString(R.string.app_name))
            setContentText(NOTIFICATION_TEXT)
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            priority = NotificationManager.IMPORTANCE_DEFAULT
            setContentIntent(notePendingIntent)
            addAction(
                NotificationCompat.Action(
                    0,
                    "Exit",
                    exitPendingIntent
                )
            )
            startForeground(CODE_FOREGROUND_SERVICE, build())
        }

    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val command = intent.getStringExtra(INTENT_COMMAND)
        val data:ArrayList<WordDefinitionTuple> = intent.extras!!.getParcelableArrayList<WordDefinitionTuple>("FloatingWindowExtra") as ArrayList<WordDefinitionTuple>
        Log.d(TAG, "initializeFloatingWindow: ${data.size}")
//        Log.d(TAG, "initializeFloatingWindow: ${data[1].definitions}")
        // Exit the service if we receive the EXIT command.
        // START_NOT_STICKY is important here, we don't want
        // the service to be relaunched.
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        // Be sure to show the notification first for all commands.
        // Don't worry, repeated calls have no effects.
        showNotification()

        // Show the floating window for adding a new note.
        if (command == INTENT_COMMAND_NOTE) {
            Toast.makeText(
                this,
                "Floating window to be added in the next lessons.",
                Toast.LENGTH_SHORT
            ).show()
        }

        initializeFloatingWindow(data)
        return START_STICKY
    }
}