package com.amindadgar.mydictionary.Utils

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.UiUtils.DraggableTouchListener


class FloatingWindow (private val context: Context) {
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.floating_window_layout,null)

    init {
        rootView.findViewById<View>(R.id.FloatingWindowContainer).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )
    }

    fun View.registerDraggableTouchListener(
        initialPosition: () -> Point,
        positionListener: (x: Int, y: Int) -> Unit
    ) {
        DraggableTouchListener(context, this, initialPosition, positionListener)
    }
    private val windowParams = WindowManager.LayoutParams(0,0,0,0
    ,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT)
    private fun getCurrentDisplayMetrics():DisplayMetrics{
        var dm = DisplayMetrics()
        // get Window metrics
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            windowManager.defaultDisplay.getMetrics(dm)
        else
           dm = context.resources.displayMetrics

        return dm
    }
    private fun calculateSizeAndPosition(
        params: WindowManager.LayoutParams,
        widthInDp: Int,
        heightInDp: Int
    ) {
        val dm = getCurrentDisplayMetrics()
        // We have to set gravity for which the calculated position is relative.
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()
        params.x = (dm.widthPixels - params.width) / 2
        params.y = (dm.heightPixels - params.height) / 2
    }

    private fun initWindowParams() {
        calculateSizeAndPosition(windowParams, 300, 150)
    }

    private fun initWindow() {
        // Using kotlin extension for views caused error, so good old findViewById is used
        rootView.findViewById<View>(R.id.closeIcon).setOnClickListener { close() }
        rootView.findViewById<View>(R.id.next_icon).setOnClickListener {
            Toast.makeText(context, "Adding notes to be implemented.", Toast.LENGTH_SHORT).show()
        }
    }

    init {
        initWindowParams()
        initWindow()
    }

    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }
    private fun setPosition(x:Int,y:Int){
        windowParams.x = x
        windowParams.y = y
        update()
    }
    private fun update(){
        try {
            windowManager.updateViewLayout(rootView,windowParams)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

}