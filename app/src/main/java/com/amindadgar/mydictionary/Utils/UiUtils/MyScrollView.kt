package com.amindadgar.mydictionary.Utils.UiUtils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import kotlin.math.abs


class MyScrollView(context: Context, attributeSet: AttributeSet?):NestedScrollView(
    context,
    attributeSet
){
    private val DEBUG_TAG = "myScrollView"
    private var mVelocityTracker: VelocityTracker? = null
    private var fragmentManager:FragmentManager?=null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        super.onTouchEvent(ev)
        Log.d(DEBUG_TAG, "onTouchEvent: ${getChildAt(0).height}")

        return myGestureDetector(ev)
    }

    // this function is used to close the initialized fragment
    fun initializeFragmentManager(fragmentManager: FragmentManager){
        this.fragmentManager = fragmentManager
    }
    // delete any fragment manager added!
    fun disposeFragmentManager(){
        this.fragmentManager = null
    }
    // we would detect touch speed in velocity and if it was less than -1000
    // we would close fragment
    private fun myGestureDetector(event: MotionEvent?):Boolean{
        val action = event!!.actionMasked


        return when (action) {
            MotionEvent.ACTION_DOWN -> {

                Log.d(DEBUG_TAG, "Action was DOWN")
                // Reset the velocity tracker back to its initial state.
                mVelocityTracker?.clear()
                // If necessary retrieve a new VelocityTracker object to watch the
                // velocity of a motion.
                mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                // Add a user's movement to the tracker.
                mVelocityTracker?.addMovement(event)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(DEBUG_TAG, "Action was MOVE ")
                mVelocityTracker?.apply {
                    val pointerId: Int = event.getPointerId(event.actionIndex)
                    addMovement(event)
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    computeCurrentVelocity(1000)
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
                    val xVelocity = getXVelocity(pointerId)
                    val yVelocity = getYVelocity(pointerId)
                    Log.d(DEBUG_TAG, "myGestureDetector Y: $scrollY")
                    Log.d(DEBUG_TAG, "myGestureDetector X: $scrollX")
                    if (abs(xVelocity) > 1000 && scrollY < 10 )
                        fragmentManager!!.popBackStack()

                    Log.d(DEBUG_TAG, "myGestureDetector xVelocity: ${abs(xVelocity) }")
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d(DEBUG_TAG, "Action was UP ")
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(DEBUG_TAG, "Action was CANCEL")
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker?.recycle()
                mVelocityTracker = null
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                Log.d(DEBUG_TAG, "Movement occurred outside bounds of current screen element")
                true
            }
            else -> super.onTouchEvent(event)
        }
    }
}