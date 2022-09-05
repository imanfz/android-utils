package com.imanfz.utility.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.imanfz.utility.GlideApp
import com.imanfz.utility.isConnectionOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

/**
 * Created by Iman Faizal on 21/May/2022
 **/

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

fun ContentResolver.getCursorContent(uri: Uri): String? = kotlin.runCatching {
    query(uri, null, null, null, null)?.use { cursor ->
        val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) { cursor.getString(nameColumnIndex) } else null
    }
}.getOrNull()

fun Context.getFileName(uri: Uri): String? {
    when(uri.scheme) {
        ContentResolver.SCHEME_FILE -> {
            val filePath = uri.path
            if (!filePath.isNullOrEmpty()) return File(filePath).name
        }
        ContentResolver.SCHEME_CONTENT -> return contentResolver.getCursorContent(uri)
    }

    return null
}

fun Context.getFileFromAsset(fileName: String): File? {
    val file = File("$cacheDir/$fileName")
    if (!file.exists()) try {
        val buffer = ByteArray(1024)
        assets.open(fileName).apply {
            read(buffer)
            close()
        }

        FileOutputStream(file).apply {
            write(buffer)
            close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return file
}

fun Context.getJsonDataFromAsset(fileName: String): String? {
    val jsonString: String
    try {
        jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return jsonString
}

fun Context.clearDiskCache() {
    // for background thread
    Thread {
        kotlin.run {
            GlideApp.get(applicationContext).clearDiskCache()
        }
    }.start()
}

fun Context.clearMemory() {
    GlideApp.get(this.applicationContext).clearMemory()
}
