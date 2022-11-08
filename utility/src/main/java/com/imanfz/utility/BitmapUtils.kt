package com.imanfz.utility

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import com.imanfz.utility.extension.getAppName
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Iman Faizal on 08/Sep/2022
 **/

fun Bitmap.circledBitmap(): Bitmap? {
    return try {
        val output =
            Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, this.width, this.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            (this.width / 2).toFloat(),
            (this.height).toFloat() - 90f,
            80f,
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, rect, rect, paint)
        output
    } catch (ex: Exception) {
        ex.printStackTrace()
        this
    }
}

fun Bitmap.toFile(context: Context, bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
    //create a file to write bitmap data
    return try {
        val imageDir = "DCIM/${context.getAppName()}"
        val dir = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            Environment.getExternalStoragePublicDirectory(imageDir)
        else context.getExternalFilesDir(imageDir)

        if(dir?.exists() != true) dir?.mkdirs()

        val file = File(dir, fileNameToSave)
        file.createNewFile()
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapData = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null // it will return null
    }
}

fun Bitmap.resizeWithRatio(maxLength: Int): Bitmap {
    return try {
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            if (width < maxLength) return this

            val ratio = height.toDouble() / width.toDouble()
            targetWidth = maxLength
            targetHeight = (maxLength * ratio).toInt()
        } else {
            if (height < maxLength) return this

            val ratio = width.toDouble() / height.toDouble()
            targetHeight = maxLength
            targetWidth = (maxLength * ratio).toInt()
        }

        Bitmap.createScaledBitmap(this, targetWidth, targetHeight, false)
    } catch (ex: Exception) {
        ex.printStackTrace()
        this
    }
}