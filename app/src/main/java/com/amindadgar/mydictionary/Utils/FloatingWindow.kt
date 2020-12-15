package com.amindadgar.mydictionary.Utils

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.UiUtils.DraggableTouchListener
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple


class FloatingWindow (private val context: Context,
                      private var data: ArrayList<WordDefinitionTuple>
                    ,private var floatingWindowWidth:Int = 300,
                      private var floatingWindowHeight:Int = 150) {
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.floating_window_layout,null)
    private var index = 0
    private val TAG = "FloatingWindow"



    private var windowIsClosed = false

    /**
     * @param windowIsClosed is used to get to know is floating window close or not
     */

    private val windowParams = WindowManager.LayoutParams(0,0,0,0
        ,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT)

    private val WordTextView:TextView
    private val DefinitionTextView:TextView


    init {
        initWindowParams()
        initWindow()
        WordTextView = rootView.findViewById<TextView>(R.id.word_TextView)
        DefinitionTextView = rootView.findViewById<TextView>(R.id.definition_text)

        rootView.findViewById<View>(R.id.FloatingWindowContainer).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )
        // initialize first item
        setWord(data[0].words,data[0].definitions)
        initClickListeners()
    }

    // this function is used to know the window is open or closed!
    fun getWindowStatus():Boolean = windowIsClosed

    fun refreshData(data: ArrayList<WordDefinitionTuple>){
        this.data = data
        setWord(data[0].words,data[0].definitions)
    }
    private fun setWord(word:String,Definition:String){
        WordTextView.text = word
        DefinitionTextView.text = Definition
    }
    // this function is for initializing next or previous button click Listeners
    private fun initClickListeners(){
        rootView.findViewById<View>(R.id.next_icon).setOnClickListener {
            Log.d(TAG, "initClickListeners: Show next item")
            // if all words was shown start from zero
            if (data.size != 1) {
                index = (index + 1) % (data.size - 1)
            }

            setWord(data[index].words,data[index].definitions)
        }
        rootView.findViewById<ImageView>(R.id.previous_icon).setOnClickListener {
            Log.d(TAG, "initClickListeners: Show previous item")
            // check data size bounds
            index = (index - 1) % (data.size - 1)
            // if we got index == -1 then make index to the last item
            if (index == -1) index = data.size - 1
            setWord(data[index].words,data[index].definitions)
        }
    }

    fun View.registerDraggableTouchListener(
        initialPosition: () -> Point,
        positionListener: (x: Int, y: Int) -> Unit) {
        DraggableTouchListener(context, this, initialPosition, positionListener)
    }



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
        params.gravity = Gravity.TOP or Gravity.START
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()
        params.x = (dm.widthPixels - params.width) / 2
        params.y = (dm.heightPixels - params.height) / 2
    }

    private fun initWindowParams() {
        // initialize window with default values width = 300 and height = 150
        calculateSizeAndPosition(windowParams, floatingWindowWidth, floatingWindowHeight)
    }

    private fun initWindow() {
        // Using kotlin extension for views caused error, so good old findViewById is used
        rootView.findViewById<View>(R.id.closeIcon).setOnClickListener { close() }

    }



    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
            windowIsClosed = false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this.context,"Unable to addView\nerror msg: please confirm permissions at application startup",Toast.LENGTH_LONG).show()
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    private fun close() {
        try {
            windowManager.removeView(rootView)
            windowIsClosed = true
        } catch (e: Exception) {
            e.printStackTrace()
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