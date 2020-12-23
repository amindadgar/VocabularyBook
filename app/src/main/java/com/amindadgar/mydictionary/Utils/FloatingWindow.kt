package com.amindadgar.mydictionary.Utils

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
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
    private val textRootView = layoutInflater.inflate(R.layout.floating_window_layout,null)
    private val closedRootView = layoutInflater.inflate(R.layout.floating_window_closed_layout,null)
    private var index = 0
    private val TAG = "FloatingWindow"
    enum class WindowPosition{center, endCenter, startCenter, top, bottom}


    // in the start the window is closed then by creating floating service we will open floating window
    private var windowIsClosed = true

    /**
     * @param windowIsClosed is used to get to know is floating window close or not
     * @param rootView is out actual window showing items
     * @param closedRootView is a layout that used to be shown when our rootViw is closed
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
        initWindow()
        textRootView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        )

        closedRootView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        WordTextView = textRootView.findViewById<TextView>(R.id.word_TextView)
        DefinitionTextView = textRootView.findViewById<TextView>(R.id.definition_text)

        textRootView.findViewById<View>(R.id.FloatingWindowContainer).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x,y -> setPosition(x,y) }
        )
        closedRootView.findViewById<View>(R.id.closed_floating_window).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y ->
                kotlin.run {
                    val dm = getCurrentDisplayMetrics()
                    // if the pointer was in the half left of screen put the layout at the left
                    if (x < dm.widthPixels / 2) {
                        setPosition(0 - windowParams.width / 2, y)
                    } else {
                        // if the pointer was in the half right of screen put the layout at the right
                        setPosition(dm.widthPixels - windowParams.width / 2, y)
                    }
                }
            }
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
        textRootView.findViewById<View>(R.id.next_icon).setOnClickListener {
            Log.d(TAG, "initClickListeners: Show next item")
            // if all words was shown start from zero
            if (data.size != 1) {
                index = (index + 1) % (data.size - 1)
            }

            setWord(data[index].words,data[index].definitions)
        }
        textRootView.findViewById<ImageView>(R.id.previous_icon).setOnClickListener {
            Log.d(TAG, "initClickListeners: Show previous item")
            // check data size bounds
            index = (index - 1) % (data.size - 1)
            // if we got index == -1 then make index to the last item
            if (index == -1) index = data.size - 1
            setWord(data[index].words,data[index].definitions)
        }
        closedRootView.findViewById<View>(R.id.closed_floating_window).setOnClickListener {
            open()
            // by clicking this icon we will open the floating Window again
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
        heightInDp: Int,
        windowPosition: WindowPosition
    ) {
        val dm = getCurrentDisplayMetrics()
        // We have to set gravity for which the calculated position is relative.
        params.gravity = Gravity.TOP or Gravity.START
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()

        setWindowPosition(params, windowPosition, dm)
    }
    private fun setWindowPosition(
        params: WindowManager.LayoutParams,
        windowPosition: WindowPosition,
        dm :DisplayMetrics
    ){
        when (windowPosition){
            WindowPosition.center ->{
                params.x = (dm.widthPixels - params.width) / 2
                params.y = (dm.heightPixels - params.height) / 2
            }
            WindowPosition.endCenter -> {
                params.x = (dm.widthPixels - (params.width / 2))
                params.y = (dm.heightPixels - params.height) / 2
            }
            WindowPosition.startCenter ->{
                params.x = 0
                params.y = (dm.heightPixels - params.height) / 2
            }
            WindowPosition.bottom ->{
                params.x = 0
                params.y = (dm.heightPixels - params.height)
            }
            WindowPosition.top ->{
                params.x = 0
                params.y = 0
            }
        }
    }


    private fun initWindowParams(width:Int = 300,height:Int = 150,windowPosition: WindowPosition = WindowPosition.center) {
        // initialize window with default values width = 300 and height = 150 and center position
        calculateSizeAndPosition(windowParams, width, height,windowPosition)
    }

    private fun initWindow() {
        // Using kotlin extension for views caused error, so good old findViewById is used
        textRootView.findViewById<View>(R.id.closeIcon).setOnClickListener { close() }

    }



    fun open() {
        try {
            closeTheClosingView()
            Log.d(TAG, "open: opening FloatingView")
            initWindowParams(width = 300,height = 150)
            windowManager.addView(textRootView, windowParams)
            windowIsClosed = false
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "open: ${e.message}")
            Toast.makeText(this.context,"Unable to addView\nerror msg: unable to open floating Window",Toast.LENGTH_LONG).show()
        }
    }
    private fun closeTheClosingView(){
        try {
            windowManager.removeViewImmediate(closedRootView)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    private fun close() {
        try {
            // There is a problem using animator object
            // after closing the view it would not opened again!

//            val animation = rootView.animate().apply {
//                scaleY(0f)
//                scaleX(0f)
//                duration = 500
//            }
//            animation.setListener(object :Animator.AnimatorListener{
//                override fun onAnimationStart(p0: Animator?) {}
//
//                override fun onAnimationEnd(p0: Animator?) {
//                  Log.d(TAG, "close: FloatingWindow")
//                  windowManager.removeViewImmediate(rootView)
//                  val isSuccessful = openClosedView()
//                  Log.d(TAG, "close: Closing the closeView Successful: $isSuccessful")
//                  windowIsClosed = true
//
//                }
//                override fun onAnimationCancel(p0: Animator?) {}
//                override fun onAnimationRepeat(p0: Animator?) {}
//            })
            Log.d(TAG, "close: FloatingWindow")
            windowManager.removeViewImmediate(textRootView)
            val isSuccessful = openClosedView()
            Log.d(TAG, "close: Closing the closeView Successful: $isSuccessful")
            windowIsClosed = true
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    private fun openClosedView():Boolean{
        // return true if operation is successful and false if unsuccessful
        return try {
            closedRootView.scaleX = 0f
            closedRootView.scaleY = 0f
            initWindowParams(width = 40,height = 40,windowPosition = WindowPosition.endCenter)
            windowManager.addView(closedRootView,windowParams)
            closedRootView.animate().apply {
                scaleX(1f)
                scaleY(1f)
                duration = 500
            }
            true
        }catch (ex:Exception){
            ex.printStackTrace()
            false
        }
    }
    private fun setPosition(x:Int,y:Int){
        windowParams.x = x
        windowParams.y = y
        update()
    }
    private fun update(){
        try {
            // check to update which layout
            // if the window is closed update the closedRootView
            // else update the textRootView
            if (windowIsClosed)
                windowManager.updateViewLayout(closedRootView,windowParams)
            else
                windowManager.updateViewLayout(textRootView,windowParams)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

}