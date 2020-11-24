package com.github.sample.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.WindowManager

class CanvasPlaygroundView
@JvmOverloads
constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    //custom view's height and width taken so that our overlay window does not go outside the screen
    private var mViewWidth = 0
    private var mViewHeight = 0

    //this is paint which is used to draw the frames
    private val mPaintFrame: Paint

    //this is the frame's rect
    private var mFrameRect: RectF? = null

    private var mLastX = 0f
    private var mLastY = 0f

    private var mTouchArea = TouchArea.NOTHING

    private val mMinFrameSize: Float

    private val mHandleSize: Int

    private val mTouchPadding = 20

    private var mIsEnabled = false

    //scale factor used to zoom in/zoom out the custom view
    private val mScaleDetector: ScaleGestureDetector
    private var mScaleFactor = 1f


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
        mViewWidth = viewWidth - paddingLeft - paddingRight
        mViewHeight = viewHeight - paddingTop - paddingBottom
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mViewWidth == 0 || mViewHeight == 0) return
        mFrameRect = calcFrameRect()
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.scale(
            mScaleFactor,
            mScaleFactor,
            mViewWidth / 2.toFloat(),
            mViewHeight / 2.toFloat()
        )
        canvas.drawColor(GRAY)
        if (mIsEnabled) drawCropFrame(canvas)
        canvas.restore()
    }

    private fun drawCropFrame(canvas: Canvas) {
        drawFrame(canvas)
        fillFrame(canvas)
        drawHandles(canvas)
    }

    private fun drawFrame(canvas: Canvas) {
        mPaintFrame.isAntiAlias = true
        mPaintFrame.style = Paint.Style.STROKE
        mPaintFrame.color = BLACK40
        mPaintFrame.strokeWidth = density * FRAME_STROKE_WEIGHT_IN_DP
        mFrameRect?.let { canvas.drawRect(it, mPaintFrame) }
    }

    private fun fillFrame(canvas: Canvas) {
        mPaintFrame.isAntiAlias = true
        mPaintFrame.style = Paint.Style.FILL
        mPaintFrame.color = BLUE20
        mFrameRect?.let { canvas.drawRect(it, mPaintFrame) }
    }

    private fun drawHandles(canvas: Canvas) {
        mPaintFrame.style = Paint.Style.FILL
        mPaintFrame.color = BLACK40
        canvas.drawCircle(mFrameRect!!.left, mFrameRect!!.top, mHandleSize.toFloat(), mPaintFrame)
        canvas.drawCircle(mFrameRect!!.right, mFrameRect!!.top, mHandleSize.toFloat(), mPaintFrame)
        canvas.drawCircle(
            mFrameRect!!.left,
            mFrameRect!!.bottom,
            mHandleSize.toFloat(),
            mPaintFrame
        )
        canvas.drawCircle(
            mFrameRect!!.right,
            mFrameRect!!.bottom,
            mHandleSize.toFloat(),
            mPaintFrame
        )
    }

    private fun calcFrameRect(): RectF {
        return RectF(
            START_GAP_OFFSET.toFloat(), START_GAP_OFFSET.toFloat(),
            (mViewWidth - START_GAP_OFFSET).toFloat(), (mViewHeight - START_GAP_OFFSET).toFloat()
        )
    }

    fun add() {
        mIsEnabled = true
        recreateFrameRect()
    }


    fun delete() {
        mIsEnabled = false
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mIsEnabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDown(event)
                mScaleDetector.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                onMove(event)
                if (mTouchArea != TouchArea.NOTHING) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mScaleDetector.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                onNothing()
                mScaleDetector.onTouchEvent(event)
                return true
            }
        }
        return false
    }


    private fun onDown(e: MotionEvent) {
        invalidate()
        mLastX = e.x
        mLastY = e.y
        checkTouchArea(e.x, e.y)
    }

    private fun onMove(e: MotionEvent) {
        val diffX = e.x - mLastX
        val diffY = e.y - mLastY
        when (mTouchArea) {
            TouchArea.LEFT_TOP -> moveHandleLT(diffX, diffY)
            TouchArea.RIGHT_TOP -> moveHandleRT(diffX, diffY)
            TouchArea.LEFT_BOTTOM -> moveHandleLB(diffX, diffY)
            TouchArea.RIGHT_BOTTOM -> moveHandleRB(diffX, diffY)
            TouchArea.NOTHING -> {
            }
        }
        invalidate()
        mLastX = e.x
        mLastY = e.y
    }

    private fun onNothing() {
        mTouchArea = TouchArea.NOTHING
        mScaleFactor = 1.0f
        invalidate()
    }

    private fun checkTouchArea(x: Float, y: Float) {
        if (isInsideCornerLeftTop(x, y)) {
            mTouchArea = TouchArea.LEFT_TOP
            return
        }
        if (isInsideCornerRightTop(x, y)) {
            mTouchArea = TouchArea.RIGHT_TOP
            return
        }
        if (isInsideCornerLeftBottom(x, y)) {
            mTouchArea = TouchArea.LEFT_BOTTOM
            return
        }
        if (isInsideCornerRightBottom(x, y)) {
            mTouchArea = TouchArea.RIGHT_BOTTOM
            return
        }
        mTouchArea = TouchArea.NOTHING
    }

    private fun isInsideCornerLeftTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.left
        val dy = y - mFrameRect!!.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }


    private fun isInsideCornerRightTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.right
        val dy = y - mFrameRect!!.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }

    private fun isInsideCornerLeftBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.left
        val dy = y - mFrameRect!!.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }


    private fun isInsideCornerRightBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.right
        val dy = y - mFrameRect!!.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }


    private fun moveHandleLT(diffX: Float, diffY: Float) {
        mFrameRect!!.left += diffX
        mFrameRect!!.top += diffY
        if (isWidthTooSmall) {
            val offsetX = mMinFrameSize - frameW
            mFrameRect!!.left -= offsetX
        }
        if (isHeightTooSmall) {
            val offsetY = mMinFrameSize - frameH
            mFrameRect!!.top -= offsetY
        }
        checkScaleBounds()
    }

    private fun moveHandleRT(diffX: Float, diffY: Float) {
        mFrameRect!!.right += diffX
        mFrameRect!!.top += diffY
        if (isWidthTooSmall) {
            val offsetX = mMinFrameSize - frameW
            mFrameRect!!.right += offsetX
        }
        if (isHeightTooSmall) {
            val offsetY = mMinFrameSize - frameH
            mFrameRect!!.top -= offsetY
        }
        checkScaleBounds()
    }

    private fun moveHandleLB(diffX: Float, diffY: Float) {
        mFrameRect!!.left += diffX
        mFrameRect!!.bottom += diffY
        if (isWidthTooSmall) {
            val offsetX = mMinFrameSize - frameW
            mFrameRect!!.left -= offsetX
        }
        if (isHeightTooSmall) {
            val offsetY = mMinFrameSize - frameH
            mFrameRect!!.bottom += offsetY
        }
        checkScaleBounds()
    }


    private fun moveHandleRB(diffX: Float, diffY: Float) {
        mFrameRect!!.right += diffX
        mFrameRect!!.bottom += diffY
        if (isWidthTooSmall) {
            val offsetX = mMinFrameSize - frameW
            mFrameRect!!.right += offsetX
        }
        if (isHeightTooSmall) {
            val offsetY = mMinFrameSize - frameH
            mFrameRect!!.bottom += offsetY
        }
        checkScaleBounds()
    }

    private fun checkScaleBounds() {
        val lDiff = mFrameRect!!.left
        val rDiff = mFrameRect!!.right - mViewWidth
        val tDiff = mFrameRect!!.top
        val bDiff = mFrameRect!!.bottom - mViewHeight
        if (lDiff < 0) {
            mFrameRect!!.left -= lDiff
        }
        if (rDiff > 0) {
            mFrameRect!!.right -= rDiff
        }
        if (tDiff < 0) {
            mFrameRect!!.top -= tDiff
        }
        if (bDiff > 0) {
            mFrameRect!!.bottom -= bDiff
        }
    }

    private val isWidthTooSmall: Boolean
        get() = frameW < mMinFrameSize

    private val isHeightTooSmall: Boolean
        get() = frameH < mMinFrameSize

   private fun recreateFrameRect() {
        mFrameRect = calcFrameRect()
        invalidate()
    }

    private val density: Float
        get() {
            val displayMetrics = DisplayMetrics()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                .getMetrics(displayMetrics)
            return displayMetrics.density
        }

    private fun sq(value: Float): Float {
        return value * value
    }

    private val frameW: Float
        get() = mFrameRect!!.right - mFrameRect!!.left

    private val frameH: Float
        get() = mFrameRect!!.bottom - mFrameRect!!.top

    private enum class TouchArea {
        NOTHING, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    //zoom in/out listener
    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.0f))
            invalidate()
            return true
        }
    }

    companion object {
        private const val HANDLE_SIZE_IN_DP = 8
        private const val MIN_FRAME_SIZE_IN_DP = 50
        private const val FRAME_STROKE_WEIGHT_IN_DP = 5
        private const val START_GAP_OFFSET = 100
        private const val BLACK40 = 0x66000000
        private const val GRAY = -0x2c2c2d
        private const val BLUE20 = 0x330000FF
    }

    init {
        val density = density
        mHandleSize = (density * HANDLE_SIZE_IN_DP).toInt()
        mMinFrameSize = density * MIN_FRAME_SIZE_IN_DP
        mPaintFrame = Paint()
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    }
}