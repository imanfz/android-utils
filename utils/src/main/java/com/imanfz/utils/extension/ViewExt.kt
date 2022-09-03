package com.imanfz.utils.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.imanfz.utils.common.SafeClickListener

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun View.gone() {
    visibility = View.GONE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.enabled() {
    isEnabled = true
}

fun View.disabled() {
    isEnabled = false
}

fun View.longSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.shortSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.snackBarWithAction(
    message: String,
    actionLabel: String,
    onClicked: () -> Unit
) {
    Snackbar.make(
        this, message, Snackbar.LENGTH_INDEFINITE
    ).setAction(actionLabel) {
        onClicked()
    }.show()
}

// for handling multiple start activity problem
fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}
