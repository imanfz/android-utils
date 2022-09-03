package com.imanfz.utility.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.imanfz.utility.isConnectionOn
import kotlin.random.Random

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun Context.hideKeyboard() {
    val inputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        (this as Activity).currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput((this as Activity).currentFocus, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasPermission(permission: Array<String>): Boolean {
    return permission.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.showBasicDialog(
    title: String,
    message: String,
    okClicked: (() -> Unit?)? = null,
    cancelClicked: (() -> Unit?)? = null
) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("Ok"
        ) { dialog, _ ->
            // User clicked OK button
            if (okClicked != null) {
                okClicked()
            }
            dialog.dismiss()
        }
        if (cancelClicked != null) {
            setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                // User cancelled the dialog
                cancelClicked()
                dialog.dismiss()
            }
        }
        create()
        setCancelable(false)
        show()
    }
}

fun Context.isConnectionOn(): Boolean {
    return isConnectionOn(this)
}

fun Context.copyTextToClipboard(text: String, msg: String) {
    val clipboardManager = ContextCompat.getSystemService(
        this,
        ClipboardManager::class.java
    ) as ClipboardManager

    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)

    shortToast("$msg copied to clipboard")
}

fun Context.createInitialAvatar(
    diameter: Int,
    fullName: String,
    textColor: Int? = null,
    bgColor: Int? = null,
    circleColor: Int? = null
): Bitmap? {
    val initialName = fullName.getInitials(limit = 1)
    val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

    val paintBorder = Paint().apply {
        color = circleColor?.let {
            ContextCompat.getColor(
                this@createInitialAvatar,
                it
            )
        } ?: Color.DKGRAY
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 6f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    val paintBackground = Paint().apply {
        val random = Random(System.currentTimeMillis())
        if (bgColor != null)
            color = ContextCompat.getColor(
                this@createInitialAvatar, bgColor
            )
        else setARGB(
            256,
            random.nextInt(256)/2,
            random.nextInt(256)/2,
            random.nextInt(256)/2
        )
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    val radius = diameter/2f

    Canvas(bitmap).apply {
        drawARGB(0,0,0,0) // set transparent
        drawRoundRect(rectF, radius, radius, paintBackground)
        drawCircle(radius, radius, radius , paintBorder)
        val paintText = Paint().apply {
            textSize = width - 20f
            isFakeBoldText = true
            color = textColor?.let {
                ContextCompat.getColor(
                    this@createInitialAvatar,
                    it
                )
            } ?: Color.WHITE

        }
        val x = (width/2) - (paintText.measureText(initialName)/2)
        val y = (height/2) - ((paintText.descent() + paintText.ascent()) / 2)
        drawText(initialName, x, y, paintText)
    }

    return bitmap
}

fun Context.isNightMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
//        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}

inline fun <reified T: Activity> Context.startActivity(
    extras: Bundle? = null
) {
    startActivity(
        Intent(this, T::class.java).apply {
            if (extras != null) {
                putExtras(extras)
            }
        }
    )
}

inline fun <reified T: Activity> Context.startActivityAndClearTask(
    extras: Bundle? = null
) {
    startActivity(
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            if (extras != null) {
                putExtras(extras)
            }
        }
    )
}

inline fun <reified T: Activity> Context.startActivityClearTop(
    extras: Bundle? = null
) {
    startActivity(
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (extras != null) {
                putExtras(extras)
            }
        }
    )
}