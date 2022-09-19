package com.imanfz.utility.ui

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

/**
 * Created by Iman Faizal on 17/Sep/2022
 **/

@Suppress("unused")
class CircularAnimatedDrawable(
    view: View,
    borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {

    private val fBounds by lazy { RectF() }
    private val mAnimatedView: View
    private var mValueAnimatorSweep: ValueAnimator? = null
    private var mValueAnimatorAngle: ValueAnimator? = null
    private var mModeAppearing = false
    private val mPaint: Paint
    private var mCurrentGlobalAngleOffset = 0f
    private var mCurrentGlobalAngle = 0f
    private var mCurrentSweepAngle = 0f
    private var mBorderWidth = 0f
    private var mRunning = false

    init {
        mAnimatedView = view
        mBorderWidth = borderWidth
        mPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = arcColor
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            alpha = 0
        }
        setupAnimations()
    }

    private fun setupAnimations() {
        mValueAnimatorAngle = ValueAnimator.ofFloat(0f, 360f - 2).apply {
            interpolator = ANGLE_INTERPOLATOR
            duration = ANGLE_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { p0 ->
                setCurrentSweepAngle(p0.animatedValue as Float)
                mAnimatedView.invalidate()
            }
        }

        mValueAnimatorSweep = ValueAnimator.ofFloat(0f, 360f - MIN_SWEEP_ANGLE * 2).apply {
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

            addUpdateListener { p0 ->
                setCurrentGlobalAngle(p0.animatedValue as Float)
                mAnimatedView.invalidate()
            }
        }
    }

    private fun toggleAppearingMode() {
        mModeAppearing = !mModeAppearing
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    fun setCurrentGlobalAngle(currentGlobalAngle: Float) {
        mCurrentGlobalAngle = currentGlobalAngle
        invalidateSelf()
    }

    fun setCurrentSweepAngle(currentSweepAngle: Float) {
        mCurrentSweepAngle = currentSweepAngle
        invalidateSelf()
    }

    fun getCurrentGlobalAngle(): Float {
        return mCurrentGlobalAngle
    }

    fun getCurrentSweepAngle(): Float {
        return mCurrentSweepAngle
    }

    override fun start() {
        if (isRunning) return
        mRunning = true
        mValueAnimatorAngle?.start()
        mValueAnimatorSweep?.start()
    }

    override fun stop() {
        if (!isRunning) return
        mRunning = false
        mValueAnimatorAngle?.cancel()
        mValueAnimatorSweep?.cancel()
    }

    override fun isRunning(): Boolean {
        return mRunning
    }

    override fun draw(canvas: Canvas) {
        var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
        var sweepAngle = mCurrentSweepAngle
        if (!mModeAppearing) {
            startAngle += sweepAngle
            sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + mBorderWidth / 2f + .5f
        fBounds.right = bounds.right - mBorderWidth / 2f - .5f
        fBounds.top = bounds.top + mBorderWidth / 2f + .5f
        fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f
    }

    fun dispose() {
        if (mValueAnimatorAngle != null) {
            mValueAnimatorAngle?.end()
            mValueAnimatorAngle?.removeAllUpdateListeners()
            mValueAnimatorAngle?.cancel()
        }
        mValueAnimatorAngle = null
        if (mValueAnimatorSweep != null) {
            mValueAnimatorSweep?.end()
            mValueAnimatorSweep?.removeAllUpdateListeners()
            mValueAnimatorSweep?.cancel()
        }
        mValueAnimatorSweep = null
    }

    companion object {
        val ANGLE_INTERPOLATOR: TimeInterpolator = LinearInterpolator()
        val SWEEP_INTERPOLATOR: TimeInterpolator = DecelerateInterpolator()
        const val ANGLE_ANIMATOR_DURATION = 1000
        const val SWEEP_ANIMATOR_DURATION = 1000
        const val MIN_SWEEP_ANGLE = 30F
    }
}