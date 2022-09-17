package com.imanfz.utility.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.imanfz.utility.R

/**
 * Created by Iman Faizal on 17/Sep/2022
 **/

@Suppress("unused")
class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    //Custom values
    private var mLoaderColor = ContextCompat.getColor(context, R.color.white)
    private var mButtonColor = ContextCompat.getColor(context, R.color.fbutton_default_color)
    private var mShadowColor = ContextCompat.getColor(context, R.color.fbutton_default_shadow_color)
    private var mStrokeColor = ContextCompat.getColor(context, R.color.fbutton_default_stroke_color)
    private var mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius)
    private var mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height)
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
    private var unpressedDrawable: Drawable? = null

    private var mAnimatedDrawable: CircularAnimatedDrawable? = null
    private var text = ""
    private var mCanvas: Canvas? = null

//    private val mMaxProgress = 100
//    private val mProgress = 100

    init {
        setupAttributes(context, attrs, defStyleAttr)
        text = getText().toString()
        with(TypedValue()) {
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
            foreground = ContextCompat.getDrawable(context, resourceId)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        refresh()
    }

    private fun setupAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        //Load from custom attributes
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        )

        for (i in 0 until typedArray.indexCount) {
            when (val attr = typedArray.getIndex(i)) {
                R.styleable.LoadingButton_lb_isShadowEnable -> {
                    isShadowEnabled = typedArray.getBoolean(attr, false)
                }
                R.styleable.LoadingButton_lb_buttonColor -> {
                    mButtonColor =
                        typedArray.getColor(attr, ContextCompat.getColor(context, R.color.unpressed_color))
                }
                R.styleable.LoadingButton_lb_loaderColor -> {
                    mLoaderColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.white))
                }
                R.styleable.LoadingButton_lb_shadowColor -> {
                    mShadowColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.pressed_color))
                    isShadowColorDefined = true
                }
                R.styleable.LoadingButton_lb_shadowHeight -> {
                    mShadowHeight =
                        typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height)
                }
                R.styleable.LoadingButton_lb_cornerRadius -> {
                    mCornerRadius =
                        typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius)
                }
                R.styleable.LoadingButton_lb_isLoading -> {
                    isLoading = typedArray.getBoolean(attr, false)
                }
                R.styleable.LoadingButton_lb_loaderMargin -> {
                    mLoaderMargin =
                        typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_progress_margin)
                }
                R.styleable.LoadingButton_lb_loaderWidth -> {
                    mLoaderWidth =
                        typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_progress_width)
                }
                R.styleable.LoadingButton_lb_isStrokeEnable -> {
                    isStrokeEnabled = typedArray.getBoolean(attr, false)
                }
                R.styleable.LoadingButton_lb_strokeColor -> {
                    mStrokeColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.fbutton_default_stroke_color))
                }
                R.styleable.LoadingButton_lb_strokeWidth -> {
                    mStrokeWidth =
                        typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_stroke_width)
                }
            }
        }
        typedArray.recycle()

        //Get paddingLeft, paddingRight, paddingTop, paddingBottom
        val attrsArray = intArrayOf(
            android.R.attr.paddingLeft,
            android.R.attr.paddingRight,
            android.R.attr.paddingTop,
            android.R.attr.paddingBottom
        )
        @StyleableRes
        var i = 0
        val ta = context.obtainStyledAttributes(attrs, attrsArray)
        mPaddingLeft = ta.getDimensionPixelSize(i++, mPaddingLeft)
        mPaddingRight = ta.getDimensionPixelSize(i++, mPaddingRight)
        mPaddingTop = ta.getDimensionPixelSize(i++, mPaddingTop)
        mPaddingBottom = ta.getDimensionPixelSize(i, mPaddingBottom)
        ta.recycle()
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
        if (this.isEnabled) {
            if (isShadowEnabled) {
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, mShadowColor)
            } else {
                mShadowHeight = 0
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT)
            }
        } else {
            Color.colorToHSV(mButtonColor, hsv)
            hsv[1] *= 0.60f // saturation component
            mShadowColor = Color.HSVToColor(alpha, hsv)
            val disabledColor = mShadowColor
            // Disabled button does not have shadow
            unpressedDrawable = createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT)
        }
        updateBackground(unpressedDrawable)

        //Set padding
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

        //Set shadow height
        if (isShadowEnabled && topColor != Color.TRANSPARENT) {
            //unpressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0) /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(
                0,
                0,
                mShadowHeight,
                0,
                0
            ) /*index, left, top, right, bottom*/
        }
        layerDrawable.setLayerInset(1, 0, 0, 0, mShadowHeight) /*index, left, top, right, bottom*/

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvas = canvas
        if (isLoading) {
            drawIndeterminateProgress(canvas)
            setText("")
        } else {
            if (text.isNotEmpty()) setText(text)
        }
    }

    private fun drawIndeterminateProgress(canvas: Canvas?) {
        if (mAnimatedDrawable == null) {
            val offset = (width - height) / 2
            mColorIndicator = mLoaderColor
            val left = if (isShadowEnabled) offset + mLoaderMargin + (mShadowHeight/2)
                else offset + mLoaderMargin
            val right = if (isShadowEnabled) width - offset - mLoaderMargin - (mShadowHeight/2)
                else width - offset - mLoaderMargin
            val bottom = if (isShadowEnabled) height - (mLoaderMargin + mShadowHeight)
                else height - mLoaderMargin
            val top = mLoaderMargin
            mAnimatedDrawable = CircularAnimatedDrawable(
                mLoaderWidth.toFloat(),
                mColorIndicator
            ).apply {
                setBounds(left, top, right, bottom)
                callback = this@LoadingButton
                start()
            }
        } else {
            if (canvas != null) {
                mAnimatedDrawable?.draw(canvas)
            } else return
        }
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        if (isLoading) {
            drawIndeterminateProgress(mCanvas)
            setText("")
        } else {
            if (text.isNotEmpty()) setText(text)
        }
    }

}