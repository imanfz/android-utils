package com.imanfz.utils

import android.os.Build
import java.util.*

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun getUUID(): UUID {
    return UUID.randomUUID()
}

fun getDeviceModel(): String {
    return Build.MODEL
}

fun getDeviceName(): String {
    var manufacturer = Build.MANUFACTURER
    manufacturer = manufacturer.substring(0, 1).uppercase(Locale.getDefault()) + manufacturer.substring(1)
    val model = getDeviceModel().substring(0, 1).uppercase(Locale.getDefault()) + getDeviceModel().substring(1)
    return if (model.startsWith(manufacturer)) model else "$manufacturer $model"
}

fun getDeviceAPILevel(): String {
    return Build.VERSION.SDK_INT.toString()
}

fun getDeviceOSCode(): String {
    return Build.VERSION.RELEASE
}

fun getDeviceOSName(): String {
    val fields = Build.VERSION_CODES::class.java.fields
    for (field in fields) {
        val fieldName = field.name
        var fieldValue = -1
        try {
            fieldValue = field.getInt(Any())
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        if (fieldValue == Build.VERSION.SDK_INT) {
            return fieldName
        }
    }
    return "UNSPECIFIED"
}

fun getDeviceTimeZone(): String {
    return TimeZone.getDefault().id
}

fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}