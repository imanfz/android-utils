package com.imanfz.utility.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.OpenableColumns
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.imanfz.utility.GlideApp
import com.imanfz.utility.isConnectionOn
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
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

fun Context.getFileFromAsset(fileName: String): File = File(cacheDir, fileName)
    .also {
        if (!it.exists()) {
            it.outputStream().use { cache ->
                assets.open(fileName).use { inputStream ->
                    inputStream.copyTo(cache)
                }
            }
        }
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

fun Context.openFile(file: File) {
    val uri = FileProvider.getUriForFile(
        this,
        applicationContext.packageName.toString() + ".provider",
        file
    )
    val intent = Intent(Intent.ACTION_VIEW)

    file.toString().apply {
        if (contains(".doc") || contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword")
        } else if (contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf")
        } else if (contains(".ppt") || file.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        } else if (contains(".xls") || contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        }  else if (contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf")
        } else if (contains(".wav") || contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav")
        } else if (contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif")
        } else if (contains(".jpg") || contains(".jpeg") || contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg")
        } else if (contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain")
        } else if (contains(".zip") || contains(".rar")) {
            intent.setDataAndType(uri, "application/zip")
        }  else if (contains(".apk")) {
            intent.setDataAndType(uri, "application/vnd.android")
        } else if (contains(".3gp") || contains(".mpg") ||
            contains(".mpeg") || contains(".mpe") ||
            contains(".mp4") || contains(".avi")
        ) {
            // Video files
            intent.setDataAndType(uri, "video/*")
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*")
        }
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        loge("open file error: ${e.localizedMessage}")
        longToast("No handler for this type of file.")
    }
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

fun Context.getDateDifference(
    calendar: Calendar
): String {
    val d = calendar.time
    val lastMonth = Calendar.getInstance()
    val lastWeek = Calendar.getInstance()
    val recent = Calendar.getInstance()

    lastMonth.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_MONTH)
    lastWeek.add(Calendar.DAY_OF_MONTH, -7)
    recent.add(Calendar.DAY_OF_MONTH, -2)

    return if (calendar.before(lastMonth)) {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
    } else if (calendar.after(lastMonth) && calendar.before(lastWeek)) {
        "Last Month"
    } else if (calendar.after(lastWeek) && calendar.before(recent)) {
        "Last Week"
    } else {
        "Recent"
    }
}

fun Context.addLifecycleObserver(observer: LifecycleObserver) {
    when (this) {
        is LifecycleOwner -> this.lifecycle.addObserver(observer)
        is ContextThemeWrapper -> this.baseContext.addLifecycleObserver(observer)
        is androidx.appcompat.view.ContextThemeWrapper -> this.baseContext.addLifecycleObserver(observer)
    }
}

fun Context.updateBaseContextLocale(language: String): Context {
    val locale = Locale(language)
    var mContext = this
    val resources: Resources = mContext.resources
    val configuration: Configuration = resources.configuration
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
    } else {
        configuration.locale = locale
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        mContext = mContext.createConfigurationContext(configuration)
    } else {
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    return mContext
}