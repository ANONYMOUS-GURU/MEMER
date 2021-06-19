package com.example.memer.HELPERS

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout


class AdvanceCanvasView : ConstraintLayout {
    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null
    private var mBitmapBrush: Bitmap? = null
    private var bitmapArrayList: ArrayList<Bitmap?>? = null
    private var mBitmapBrushDimensions: Vector2? = null
    private var paintLine: Paint? = null
    private val mPositions: MutableList<Vector2> = ArrayList(100)
    private var pathMap // current Paths being drawn
            : HashMap<Int, Path>? = null
    private var previousPointMap // current Points
            : HashMap<Int, Point>? = null
    private val i = 0

    private class Vector2(val x: Float, val y: Float) {
    }

    @SuppressLint("UseSparseArrays")
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) // pass context to View's constructor
    {
    }

    constructor(c: Context) : super(c) {
        pathMap = HashMap()
        previousPointMap = HashMap()
        bitmapArrayList = ArrayList()
        paintLine = Paint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmapCanvas = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        for (i in mPositions.indices) {
            canvas.drawBitmap(bitmapArrayList!![i]!!, mPositions[i].x, mPositions[i].y, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val actionIndex = event.actionIndex
        if (action == MotionEvent.ACTION_DOWN
            || action == MotionEvent.ACTION_POINTER_DOWN
        ) {
            touchStarted(
                event.getX(actionIndex), event.getY(actionIndex),
                event.getPointerId(actionIndex)
            )
        } else if (action == MotionEvent.ACTION_UP
            || action == MotionEvent.ACTION_POINTER_UP
        ) {
            touchEnded(event.getPointerId(actionIndex))
        } else {
            touchMoved(event)
        }
        invalidate()
        return true
    }

    private fun touchStarted(x: Float, y: Float, lineID: Int) {
        var path: Path = Path() // create a new Path
        pathMap!![lineID] = path // add the Path to Map
        var point: Point = Point() // create a new Point
        previousPointMap!![lineID] = point // add the Point to the Map
        path = Path() // create a new Path
        point = Point() // create a new Point
        path.moveTo(x, y)
        point.x = x.toInt()
        point.y = y.toInt()
    } // end method touchStarted

    private fun touchMoved(event: MotionEvent) {
        // for each of the pointers in the given MotionEvent
        for (i in 0 until event.pointerCount) {
            val posX = event.x
            val posY = event.y
            mPositions.add(
                Vector2(
                    posX - mBitmapBrushDimensions!!.x / 2,
                    posY - mBitmapBrushDimensions!!.y / 2
                )
            )
            bitmapArrayList!!.add(mBitmapBrush)
        }
        invalidate()
    }

    private fun touchEnded(lineID: Int) {
        pathMap!![lineID]?.reset()
    }

    fun init(bitmap: Bitmap?) {
        mBitmapBrush = bitmap
        val shader = BitmapShader(mBitmapBrush!!, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
        paintLine!!.shader = shader
        mBitmapBrushDimensions = Vector2(
            mBitmapBrush!!.width.toFloat(),
            mBitmapBrush!!.height.toFloat()
        )
    }
}