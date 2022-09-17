package com.imanfz.utility.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator


class CircularAnimatedDrawable(
    borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {

    private val ANGLE_INTERPOLATOR: TimeInterpolator = LinearInterpolator()
    private val SWEEP_INTERPOLATOR: TimeInterpolator = DecelerateInterpolator()
    private val ANGLE_ANIMATOR_DURATION = 1000
    private val SWEEP_ANIMATOR_DURATION = 1000
    val MIN_SWEEP_ANGLE = 30
    private val fBounds = RectF()

    private var mObjectAnimatorSweep: ObjectAnimator? = null
    private var mObjectAnimatorAngle: ObjectAnimator? = null
    private var mModeAppearing = false
    private var mPaint: Paint? = null
    private var mCurrentGlobalAngleOffset = 0f
    private var mCurrentGlobalAngle = 0f
    private var mCurrentSweepAngle = 0f
    private var mBorderWidth = 0f
    private var mRunning = false

    init {
        mBorderWidth = borderWidth
        mPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = arcColor
        }
        setupAnimations()
    }

    override fun draw(canvas: Canvas) {
        var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
        var sweepAngle = mCurrentSweepAngle
        if (!mModeAppearing) {
            startAngle += sweepAngle
            sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint!!)
    }

    override fun setAlpha(alpha: Int) {
        mPaint?.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint?.colorFilter = cf
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    private fun toggleAppearingMode() {
        mModeAppearing = !mModeAppearing
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + mBorderWidth / 2f + .5f
        fBounds.right = bounds.right - mBorderWidth / 2f - .5f
        fBounds.top = bounds.top + mBorderWidth / 2f + .5f
        fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f
    }

    private val mAngleProperty: Property<CircularAnimatedDrawable, Float> =
        object : Property<CircularAnimatedDrawable, Float>(
            Float::class.java, "angle"
        ) {
            override operator fun get(`object`: CircularAnimatedDrawable): Float {
                return `object`.getCurrentGlobalAngle()
            }

            override operator fun set(`object`: CircularAnimatedDrawable, value: Float) {
                `object`.setCurrentGlobalAngle(value)
            }
        }

    private val mSweepProperty: Property<CircularAnimatedDrawable, Float> =
        object : Property<CircularAnimatedDrawable, Float>(
            Float::class.java, "arc"
        ) {
            override operator fun get(`object`: CircularAnimatedDrawable): Float {
                return `object`.getCurrentSweepAngle()
            }

            override operator fun set(`object`: CircularAnimatedDrawable, value: Float) {
                `object`.setCurrentSweepAngle(value)
            }
        }

    private fun setupAnimations() {
        mObjectAnimatorAngle = ObjectAnimator.ofFloat(
            this,
            mAngleProperty,
            360f
        ).apply {
            interpolator = ANGLE_INTERPOLATOR
            duration = ANGLE_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(
                this,
            mSweepProperty,
            360f - MIN_SWEEP_ANGLE * 2
        ).apply {
            interpolator = SWEEP_INTERPOLATOR
            duration = SWEEP_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {
                    toggleAppearingMode()
                }
            })
        }
    }

    override fun start() {
        if (isRunning) {
            return
        }
        mRunning = true
        mObjectAnimatorAngle?.start()
        mObjectAnimatorSweep?.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        mRunning = false
        mObjectAnimatorAngle?.cancel()
        mObjectAnimatorSweep?.cancel()
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return mRunning
    }

    fun setCurrentGlobalAngle(currentGlobalAngle: Float) {
        mCurrentGlobalAngle = currentGlobalAngle
        invalidateSelf()
    }

    fun getCurrentGlobalAngle(): Float {
        return mCurrentGlobalAngle
    }

    fun setCurrentSweepAngle(currentSweepAngle: Float) {
        mCurrentSweepAngle = currentSweepAngle
        invalidateSelf()
    }

    fun getCurrentSweepAngle(): Float {
        return mCurrentSweepAngle
    }
}