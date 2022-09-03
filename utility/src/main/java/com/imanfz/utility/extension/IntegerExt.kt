package com.imanfz.utility.extension

import android.content.res.Resources

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()