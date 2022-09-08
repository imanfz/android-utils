package com.imanfz.utility.extension

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.*

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun Uri.getMimeType(context: Context) : String? {
    return this.let { returnUri ->
        context.contentResolver.getType(returnUri)
    }
}

fun Uri.createFile(context: Context, dir: String, filename: String) {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    try {
        val fileReader = ByteArray(4096)
        var fileSizeDownloaded: Long = 0
        val outputFile = File(dir, filename)
        inputStream = context.contentResolver.openInputStream(this)
        outputStream = FileOutputStream(File(dir, filename))
        while (true) {
            val read: Int? = inputStream?.read(fileReader)
            if (read == -1) {
                break
            }
            if (read != null) {
                outputStream.write(fileReader, 0, read)
            }
            fileSizeDownloaded += read?.toLong() ?: 0
        }
        outputStream.flush()
        outputFile.absolutePath
    } catch (e: IOException) {
        throw e
    } finally {
        inputStream?.close()
        outputStream?.close()
    }
}

fun Uri.getAudioDuration(context: Context) : String? {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, this)
    } catch (e: RuntimeException) {
        loge("Cannot retrieve audio file ${e.localizedMessage}")
    }
    val metadataDurationValue = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    return if(metadataDurationValue != null && !metadataDurationValue.isBlank()) {
        (Integer.parseInt(metadataDurationValue) / 1000).formatSecondsTime()
    }else{
        0.formatSecondsTime()
    }
}

fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}

