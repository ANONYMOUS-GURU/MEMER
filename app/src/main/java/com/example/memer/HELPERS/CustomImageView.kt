package com.example.memer.HELPERS

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.memer.FRAGMENTS.FragmentAddPost

class CustomImageView : ImageView {

    private var MODE = CustomImageView.NONE
    private var oldDist = 1f
    private var d = 0f
    private var newRot = 0f
    private  var scalediff:Float = 0f
    private var viewTouched = -1


    /*
     CREDITS - https://github.com/lau1944/Zoom-Drag-Rotate-ImageView
     */
    var parms: RelativeLayout.LayoutParams? = null
    var startwidth = 0
    var startheight = 0
    var dx = 0f
    var dy = 0f
    var x_ = 0f
    var y_ = 0f
    var angle = 0f

    companion object{
        private const val NONE = 0;
        private const val DRAG = 1;
        private const val ZOOM = 2;
    }


    constructor(context: Context?):super(context) {
    }
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs) {
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }


}