package com.example.firstkotlinapp.util

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.customview.widget.ViewDragHelper

class DragKLayout(context: Context) : FrameLayout(context){
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("TouchEventTest", "called dispatchTouchEvent() in CustomLayout")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("TouchEventTest", "called onInterceptTouchEvent() in CustomLayout")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("TouchEventTest", "called onTouchEvent() in CustomLayout")
        return super.onTouchEvent(event)
    }


}
