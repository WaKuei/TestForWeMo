package com.example.testforwemo.view.custom

import android.content.Context
import android.util.AttributeSet
import com.google.android.gms.maps.MapView
import android.view.MotionEvent
import timber.log.Timber

class CustomMapView (context: Context, attributeSet: AttributeSet) : MapView(context, attributeSet) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.parent.requestDisallowInterceptTouchEvent(true)
                Timber.d("Inside if of action down")
            }
            MotionEvent.ACTION_UP -> {
                parent.parent.requestDisallowInterceptTouchEvent(false)
                Timber.d("Inside if of action up")
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}