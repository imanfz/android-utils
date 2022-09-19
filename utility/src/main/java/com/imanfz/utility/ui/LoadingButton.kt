package com.imanfz.utility.ui

import android.animation.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.imanfz.utility.R
import com.imanfz.utility.extension.addLifecycleObserver
import com.imanfz.utility.extension.updateHeight
import com.imanfz.utility.extension.updateWidth
import kotlin.math.abs

/**
 * Created by Iman Faizal on 17/Sep/2022
 **/

@Suppress("unused")
class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), View.OnTouchListener, LifecycleEventObserver {

    //Custom values
    private var isCircularButton = false
    private var mLoaderColor = ContextCompat.getColor(context, R.color.white)
    private var mButtonColor = ContextCompat.getColor(context, R.color.fbutton_default_color)
    private var mShadowColor = ContextCompat.getColor(context, R.color.fbutton_default_shadow_color)
    private var mStrokeColor = ContextCompat.getColor(context, R.color.fbutton_default_stroke_color)
    private var mPressedColor = ContextCompat.getColor(context, R.color.blackTransparent20)
    private var mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius)
    private var mShadowHeight = 0
    private var mStrokeWidth = resources.getDimensionPixelSize(R.dimen.fbutton_default_stroke_width)
    private var mLoaderWidth = resources.getDimensionPixelSize(R.dimen.fbutton_default_progress_width)
    private var mLoaderMargin = resources.getDimensionPixelSize(R.dimen.fbutton_default_progress_margin)
    private var mColorIndicator = ContextCompat.getColor(context, R.color.fbutton_progress_color)
    private var isLoading = false
    private var isShadowEnabled = false
    private var isShadowColorDefined = false
    private var isStrokeEnabled = false

    //Native values
    private var mPaddingLeft = resources.getDimensionPixelSize(R.dimen.fbutton_default_padding_left)
    private var mPaddingRight = resources.getDimensionPixelSize(R.dimen.fbutton_default_padding_right)
    private var mPaddingTop = resources.getDimensionPixelSize(R.dimen.fbutton_default_padding_top)
    private var mPaddingBottom = resources.getDimensionPixelSize(R.dimen.fbutton_default_padding_bottom)

    //Background drawable
    private lateinit var pressedDrawable: Drawable
    private lateinit var unpressedDrawable: Drawable

    private var mAnimatedDrawable: CircularAnimatedDrawable? = null
    private var text = ""
    private lateinit var mCanvas: Canvas
    private val initWidth: Int by lazy { width }
    private val finalWidth: Int by lazy {
        height - (abs(mPaddingTop - mPaddingLeft) * 2)
    }

    private val initHeight: Int by lazy { height }
    private val finalHeight: Int by lazy { height }
    private val initTextSize: Float by lazy { textSize }

//    private val mMaxProgress = 100
//    private val mProgress = 100

    private val startAnimator by lazy {
        AnimatorSet().apply {
            if (isCircularButton) {
                playTogether(
                    widthAnimator(initWidth, finalWidth),
                    heightAnimator(initHeight, finalHeight),
                    alphaAnimator(0, 255),
                    textSizeAnimator(initTextSize, 0f)
                )
            } else {
                playTogether(
                    alphaAnimator(0, 255),
                    textSizeAnimator(initTextSize, 0f)
                )
            }
        }
    }

    private val reverseAnimator by lazy {
        AnimatorSet().apply {
            if (isCircularButton) {
                playTogether(
                    widthAnimator(finalWidth, initWidth),
                    heightAnimator(finalHeight, initHeight),
                    alphaAnimator(255, 0),
                    textSizeAnimator(0f, initTextSize)
                )
            } else {
                playTogether(
                    alphaAnimator(255, 0),
                    textSizeAnimator(0f, initTextSize)
                )
            }
        }
    }

    init {
        setupAttributes(context, attrs, defStyleAttr)
        context.addLifecycleObserver(this)
        text = getText().toString()
        textAlignment = TEXT_ALIGNMENT_CENTER
        setOnTouchListener(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        refresh()
    }

    private fun setupAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        //Load from custom attributes
        context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        ).apply {
            for (i in 0 until indexCount) {
                when (val attr = getIndex(i)) {
                    R.styleable.LoadingButton_lb_isShadowEnable -> {
                        isShadowEnabled = getBoolean(attr, false)
                    }
                    R.styleable.LoadingButton_lb_buttonColor -> {
                        mButtonColor = getColor(attr, ContextCompat.getColor(context, R.color.unpressed_color))
                    }
                    R.styleable.LoadingButton_lb_loaderColor -> {
                        mLoaderColor = getColor(attr, ContextCompat.getColor(context, R.color.white))
                    }
                    R.styleable.LoadingButton_lb_shadowColor -> {
                        mShadowColor = getColor(attr, ContextCompat.getColor(context, R.color.pressed_color))
                        isShadowColorDefined = true
                    }
                    R.styleable.LoadingButton_lb_shadowHeight -> {
                        mShadowHeight = getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height)
                    }
                    R.styleable.LoadingButton_lb_cornerRadius -> {
                        mCornerRadius = getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius)
                    }
                    R.styleable.LoadingButton_lb_isCircular -> {
                        isCircularButton = getBoolean(attr, false)
                    }
                    R.styleable.LoadingButton_lb_isLoading -> {
                        isLoading = getBoolean(attr, false)
                    }
                    R.styleable.LoadingButton_lb_loaderMargin -> {
                        mLoaderMargin = getDimensionPixelSize(attr, R.dimen.fbutton_default_progress_margin)
                    }
                    R.styleable.LoadingButton_lb_loaderWidth -> {
                        mLoaderWidth = getDimensionPixelSize(attr, R.dimen.fbutton_default_progress_width)
                    }
                    R.styleable.LoadingButton_lb_isStrokeEnable -> {
                        isStrokeEnabled = getBoolean(attr, false)
                    }
                    R.styleable.LoadingButton_lb_strokeColor -> {
                        mStrokeColor = getColor(attr, ContextCompat.getColor(context, R.color.fbutton_default_stroke_color))
                    }
                    R.styleable.LoadingButton_lb_strokeWidth -> {
                        mStrokeWidth = getDimensionPixelSize(attr, R.dimen.fbutton_default_stroke_width)
                    }
                }
            }
            recycle()
        }

        //Get paddingLeft, paddingRight, paddingTop, paddingBottom
        val attrsArray = intArrayOf(
            android.R.attr.paddingLeft,
            android.R.attr.paddingRight,
            android.R.attr.paddingTop,
            android.R.attr.paddingBottom
        )
        @StyleableRes var i = 0
        context.obtainStyledAttributes(attrs, attrsArray).apply {
            mPaddingLeft = getDimensionPixelSize(i++, mPaddingLeft)
            mPaddingRight = getDimensionPixelSize(i++, mPaddingRight)
            mPaddingTop = getDimensionPixelSize(i++, mPaddingTop)
            mPaddingBottom = getDimensionPixelSize(i, mPaddingBottom)
            recycle()
        }
    }

    private fun refresh() {
        val alpha = Color.alpha(mButtonColor)
        val hsv = FloatArray(3)
        Color.colorToHSV(mButtonColor, hsv)
        hsv[2] *= 0.8f // value

        //if shadow color was not defined, generate shadow color = 80% brightness
        if (!isShadowColorDefined) {
            mShadowColor = Color.HSVToColor(alpha, hsv)
        }

        //Create pressed background and unpressed background drawables
        if (isEnabled) {
            pressedDrawable = createDrawable(mCornerRadius, mPressedColor, mButtonColor)
            unpressedDrawable = if (isShadowEnabled) {
                createDrawable(mCornerRadius, mButtonColor, mShadowColor)
            } else {
                createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT)
            }
        } else {
            Color.colorToHSV(mButtonColor, hsv)
            hsv[1] *= 0.60f // saturation component
            mShadowColor = Color.HSVToColor(alpha, hsv)
            val disabledColor = mShadowColor
            // Disabled button does not have shadow
            pressedDrawable = createDrawable(mCornerRadius, disabledColor, mPressedColor)
            unpressedDrawable = createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT)
        }

        updateBackground(unpressedDrawable)
        setPadding(
            mPaddingLeft,
            mPaddingTop,
            mPaddingRight,
            mPaddingBottom + mShadowHeight
        )
    }

    private fun updateBackground(background: Drawable?) {
        if (background == null) return
        //Set button background
        this.background = background
    }

    private fun createDrawable(radius: Int, topColor: Int, bottomColor: Int): LayerDrawable {
        val outerRadius = floatArrayOf(
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat()
        )

        //Top
        val topShapeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = outerRadius
            setColor(topColor)

            //Create Stroke/Border
            if (isStrokeEnabled) {
                setStroke(mStrokeWidth, mStrokeColor)
            }
        }
        //Bottom
        val bottomShapeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = outerRadius
            setColor(bottomColor)
        }

        //Create array
        val drawArray = arrayOf<Drawable>(bottomShapeDrawable, topShapeDrawable)
        val layerDrawable = LayerDrawable(drawArray)

        if (isShadowEnabled && topColor != Color.TRANSPARENT) {
            layerDrawable.setLayerInset(0, 0, 0, 0, 0)
        } else {
            layerDrawable.setLayerInset(0, 0, mShadowHeight, 0, 0)
        }
        layerDrawable.setLayerInset(1, 0, 0, 0, if (isShadowEnabled) mShadowHeight else 0)

        return layerDrawable
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        refresh()
    }

    //Getter
    override fun getShadowColor(): Int {
        return mShadowColor
    }

    fun isShadowEnabled(): Boolean {
        return isShadowEnabled
    }

    fun isStrokeEnabled(): Boolean {
        return isStrokeEnabled
    }

    fun isProgressEnabled(): Boolean {
        return isLoading
    }

    fun getButtonColor(): Int {
        return mButtonColor
    }

    fun getShadowHeight(): Int {
        return mShadowHeight
    }

    fun getButtonText(): String {
        return text
    }

    //Setter
    fun setCircular(value: Boolean) {
        isCircularButton = value
    }

    fun setShadowEnabled(isShadowEnabled: Boolean) {
        this.isShadowEnabled = isShadowEnabled
        refresh()
    }

    fun setStrokeEnabled(isStrokeEnabled: Boolean) {
        this.isStrokeEnabled = isStrokeEnabled
        refresh()
    }

    fun setButtonColor(buttonColor: Int) {
        mButtonColor = buttonColor
        refresh()
    }

    fun setStrokeColor(strokeColor: Int) {
        mStrokeColor = strokeColor
        refresh()
    }

    fun setProgressColor(progressColor: Int) {
        mLoaderColor = progressColor
        refresh()
    }

    fun setShadowColor(shadowColor: Int) {
        mShadowColor = shadowColor
        isShadowColorDefined = true
        refresh()
    }

    fun setButtonText(text: String) {
        this.text = text
        invalidate()
        requestLayout()
    }

    fun setShadowHeight(shadowHeight: Int) {
        mShadowHeight = shadowHeight
        refresh()
    }

    fun setStrokeWidth(strokeWidth: Int) {
        mStrokeWidth = strokeWidth
        refresh()
    }

    fun setProgressWidth(progressWidth: Int) {
        mLoaderWidth = progressWidth
        refresh()
    }

    fun setProgressMargin(progressMargin: Int) {
        mLoaderMargin = progressMargin
        refresh()
    }

    fun setCornerRadius(cornerRadius: Int) {
        mCornerRadius = cornerRadius
        refresh()
    }

    fun showLoading() {
        setLoading(true)
    }

    fun hideLoading() {
        setLoading(false)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isClickable) updateBackground(pressedDrawable)
            }
            MotionEvent.ACTION_MOVE -> {
                val r = Rect()
                view.getLocalVisibleRect(r)
                if (!r.contains(motionEvent.x.toInt(), motionEvent.y.toInt() + 3 * mShadowHeight) &&
                    !r.contains(motionEvent.x.toInt(), motionEvent.y.toInt() - 3 * mShadowHeight)
                ) {
                    updateBackground(unpressedDrawable)
                }
            }
            MotionEvent.ACTION_OUTSIDE -> {}
            MotionEvent.ACTION_CANCEL -> {}
            MotionEvent.ACTION_UP -> {
                updateBackground(unpressedDrawable)
            }
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (isCircularButton) {
            mCornerRadius = widthSize / 2
            refresh()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvas = canvas
        if (isLoading) {
            drawIndeterminateProgress(canvas)
            setText("")
            if (isCircularButton && isInEditMode) {
                updateWidth(finalWidth)
                updateHeight(finalHeight)
                invalidate()
                requestLayout()
            }
        } else {
            if (isCircularButton && isInEditMode) {
                updateWidth(initWidth)
                updateHeight(initHeight)
                invalidate()
                requestLayout()
            }
            if (text.isNotEmpty()) setText(text)
        }
        if (isInEditMode) {
            mAnimatedDrawable?.alpha = 255
        }
    }

    private fun drawIndeterminateProgress(canvas: Canvas) {
        if (mAnimatedDrawable == null) {
            if (isCircularButton) {
                val offset = (finalWidth - finalHeight) / 2
                mColorIndicator = mLoaderColor
                val left = if (isShadowEnabled) offset + mLoaderMargin + (mShadowHeight / 2)
                else offset + mLoaderMargin
                val right =
                    if (isShadowEnabled) finalWidth - offset - mLoaderMargin - (mShadowHeight / 2)
                    else finalWidth - offset - mLoaderMargin
                val bottom = if (isShadowEnabled) finalHeight - (mLoaderMargin + mShadowHeight)
                else finalHeight - mLoaderMargin
                val top = mLoaderMargin
                mAnimatedDrawable = CircularAnimatedDrawable(
                    this,
                    mLoaderWidth.toFloat(),
                    mColorIndicator
                ).apply {
                    setBounds(left, top, right, bottom)
                    callback = this@LoadingButton
                }
            } else {
                val offset = (initWidth - initHeight) / 2
                mColorIndicator = mLoaderColor
                val left = if (isShadowEnabled) offset + mLoaderMargin + (mShadowHeight / 2)
                else offset + mLoaderMargin
                val right =
                    if (isShadowEnabled) initWidth - offset - mLoaderMargin - (mShadowHeight / 2)
                    else initWidth - offset - mLoaderMargin
                val bottom = if (isShadowEnabled) initHeight - (mLoaderMargin + mShadowHeight)
                else initHeight - mLoaderMargin
                val top = mLoaderMargin
                mAnimatedDrawable = CircularAnimatedDrawable(
                    this,
                    mLoaderWidth.toFloat(),
                    mColorIndicator
                ).apply {
                    setBounds(left, top, right, bottom)
                    callback = this@LoadingButton
                }
            }
        } else {
            mAnimatedDrawable?.apply {
                draw(canvas)
                start()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        if (isLoading) {
            drawIndeterminateProgress(mCanvas)
//            setText("")
            startAnimation()
        } else {
            reverseAnimation()
//            if (text.isNotEmpty()) setText(text)
        }
    }

    private fun startAnimation() {
        startAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    isClickable = false
                    mAnimatedDrawable?.start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    removeAllListeners()
                }
            })
            start()
        }
    }

    private fun reverseAnimation() {
        reverseAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isClickable = true
                    mAnimatedDrawable?.stop()
                    removeAllListeners()
                }
            })
            start()
        }
    }

    private fun dispose() {
        startAnimator.disposeAnimator()
        reverseAnimator.disposeAnimator()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_DESTROY -> {
                mAnimatedDrawable?.dispose()
                dispose()
            }
            else -> {}
        }
    }

    private fun widthAnimator(initial: Int, final: Int) =
        ValueAnimator.ofInt(initial, final).apply {
            addUpdateListener { animation ->
                updateWidth(animation.animatedValue as Int)
                invalidate()
                requestLayout()
            }
        }

    private fun heightAnimator(initial: Int, final: Int) =
        ValueAnimator.ofInt(initial, final).apply {
            addUpdateListener { animation ->
                updateHeight(animation.animatedValue as Int)
                invalidate()
                requestLayout()
            }
        }

    private fun alphaAnimator(initial: Int, final: Int) = ValueAnimator.ofInt(
        initial,
        final
    ).apply {
        addUpdateListener {
            mAnimatedDrawable?.alpha = it.animatedValue as Int
            mAnimatedDrawable?.invalidateSelf()
        }
    }

    private fun textSizeAnimator(initial: Float, final: Float) = ValueAnimator.ofFloat(
        initial,
        final
    ).apply {
        interpolator = OvershootInterpolator()
    }

}

internal fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}