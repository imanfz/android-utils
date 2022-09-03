package com.imanfz.androidutils.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(format, locale).format(this)
}