package com.imanfz.utility.extension

import android.graphics.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Iman Faizal on 08/Sep/2022
 **/

fun Bitmap.getCircledBitmap(): Bitmap? {
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
    return output
}

fun Bitmap.convertBitmapToFile(destinationFile: File?, quality:Int = 70) {
    val file = destinationFile ?: return

    file.createNewFile()
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, bos)
    val bitmapData = bos.toByteArray()
    logd("convertBitmapToFile: ${bitmapData.size / 1024}")
    val fos = FileOutputStream(file)
    fos.write(bitmapData)
    fos.flush()
    fos.close()
}