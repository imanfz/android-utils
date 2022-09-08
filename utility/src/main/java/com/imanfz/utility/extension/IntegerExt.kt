package com.imanfz.utility.extension

import android.content.res.Resources

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.formatSecondsTime() :String {
    return if ((this / (60 * 60)) >= 1){
        String.format("%02d", (this / (60 * 60))) + ":" + String.format("%02d", this / 60 % 60) + ":" + String.format("%02d", this % 60)
    } else {
        String.format("%02d", this / 60 % 60) + ":" + String.format("%02d", this % 60)
    }
}