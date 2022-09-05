package com.imanfz.utility

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import java.util.*

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

@GlideModule
class AppGlideModule: AppGlideModule() {

    companion object {
        const val MINUTE = "MINUTE"
        const val HOUR = "HOUR"
        const val DAY = "DAY"
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f)
            .build()
        val memoryCacheSizeBytes: Long = calculator.memoryCacheSize.toLong() //1024 * 1024 * 20 // 20mb
        builder.apply {
            setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
            setDiskCache(InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes))
            setDefaultRequestOptions(requestOptions(context))
            if (BuildConfig.DEBUG) {
                setLogLevel(Log.DEBUG)
            } else {
                setLogLevel(Log.INFO)
            }
        }
    }

    private fun requestOptions(context: Context): RequestOptions {
        return RequestOptions()
            .signature(ObjectKey(signatureDate(DAY)))
//            .override(200, 200)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.progress_animation))
            .error(ContextCompat.getDrawable(context, R.drawable.placeholder_broken_image))
//            .centerCrop()
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .encodeQuality(70)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .format(DecodeFormat.PREFER_ARGB_8888)
    }

    private fun signatureDate(type : String): Int {
        val calendar = Calendar.getInstance()
        var curVersion = -1
        // cache signature expire by ...
        when (type) {
            DAY -> curVersion = calendar[Calendar.DAY_OF_MONTH] + calendar[Calendar.MONTH] + calendar[Calendar.YEAR]
            HOUR -> curVersion = calendar[Calendar.HOUR_OF_DAY] + calendar[Calendar.DAY_OF_MONTH] + calendar[Calendar.MONTH] + calendar[Calendar.YEAR]
            MINUTE -> curVersion = calendar[Calendar.MINUTE] + calendar[Calendar.HOUR_OF_DAY] + calendar[Calendar.DAY_OF_MONTH] + calendar[Calendar.MONTH] + calendar[Calendar.YEAR]
        }
        return curVersion
    }
}