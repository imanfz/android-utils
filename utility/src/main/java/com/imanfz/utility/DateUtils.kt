package com.imanfz.utility

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun getCurrentDateTime(format: String = "dd/MM/yyyy"): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}

fun getCurrentDateTimeMils(): Long {
    return System.currentTimeMillis()
}

fun showDatePicker(
    context: Context,
    onSelected: (date: String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val years = calendar.get(Calendar.YEAR)
    val months = calendar.get(Calendar.MONTH)
    val days = calendar.get(Calendar.DAY_OF_MONTH)
    DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            onSelected(formatDate(year, monthOfYear, dayOfMonth))
        },
        years,
        months,
        days
    ).show()
}

fun formatDate(year:Int, month:Int, day:Int):String{
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    val selectedDate = calendar.time
    return SimpleDateFormat("MMM, dd yyyy", Locale.getDefault()).format(selectedDate)
}