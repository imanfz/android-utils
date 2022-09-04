package com.imanfz.utility.extension

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

// Activity related
inline fun <reified  T : Any> Activity.getValue(
    label: String, defaultValue: T? = null) = lazy {
    val value = intent?.extras?.get(label)
    if (value is T) value else defaultValue
}

inline fun <reified  T : Any> Activity.getValueNonNull(
    label: String, defaultValue: T? = null) = lazy {
    val value = intent?.extras?.get(label)
    requireNotNull((if (value is T) value else defaultValue)) { label }
}

@Suppress("DEPRECATION")
fun Activity.displayMetrics(): Size {
    // display metrics is a structure describing general information
    // about a display, such as its size, density, and font scaling
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics = windowManager.currentWindowMetrics
        Size(metrics.bounds.width(), metrics.bounds.height())
    } else {
        val display = getSystemService(WindowManager::class.java).defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        Size(metrics.widthPixels, metrics.heightPixels)
    }
}

// extension function to get the real size of the display
@Suppress("DEPRECATION")
fun Activity.realSize(): Point {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowInsets = windowManager.currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(insets, Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom))
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(windowManager.currentWindowMetrics.bounds.width() - insetsWidth, windowManager.currentWindowMetrics.bounds.height() - insetsHeight)
    } else {
        Point().apply {
            windowManager.defaultDisplay.getSize(this)
        }
    }
}

// extension function to get window bounds
fun Activity.bounds(): Rect?{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // returns the bounds of the area associated with
        // this window or visual context
        windowManager.currentWindowMetrics.bounds
    } else {
        null
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.apply {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Activity.showKeyboard() {
    Handler(Looper.getMainLooper()).postDelayed({
        currentFocus?.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }, 1000)
}

fun Activity.longSnack(text: String) {
    currentFocus?.longSnack(text)
}

fun Activity.shortSnack(text: String) {
    currentFocus?.shortSnack(text)
}
