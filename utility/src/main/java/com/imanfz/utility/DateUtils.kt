package com.imanfz.utility

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import com.imanfz.utility.extension.setSafeOnClickListener
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

fun showDatePicker(context: Context, textView: TextView) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    textView.setSafeOnClickListener {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                textView.text = formatDate(year, monthOfYear, dayOfMonth)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

fun formatDate(year:Int, month:Int, day:Int):String{
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    val selectedDate = calendar.time
    return SimpleDateFormat("MMM, dd yyyy", Locale.getDefault()).format(selectedDate)
}