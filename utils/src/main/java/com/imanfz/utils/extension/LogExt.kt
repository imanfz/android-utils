package com.imanfz.utils.extension

import android.os.Build
import android.util.Log

/**
 * Created by Iman Faizal on 31/Aug/2022
 **/

inline val <reified T> T.TAG: String
    get() {
        return if (!T::class.java.isAnonymousClass) {
            val name = T::class.java.simpleName
            if (name.length <= 23 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) name else
                name.substring(0, 23)// first 23 chars
        } else {
            val name = T::class.java.name
            if (name.length <= 23 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                name else name.substring(name.length - 23, name.length)// last 23 chars
        }
    }

inline fun <reified T> T.logv(message: String) = Log.v(TAG, message)
inline fun <reified T> T.logi(message: String) = Log.i(TAG, message)
inline fun <reified T> T.logw(message: String) = Log.w(TAG, message)
inline fun <reified T> T.logd(message: String) = Log.d(TAG, message)
inline fun <reified T> T.loge(message: String) = Log.e(TAG, message)
