package com.filipetavares.financialapp2.util

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Dates {
    companion object Dates {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun createDatePickerDialog(
            context: Context,
            calendar: Calendar,
            onDateSelected: (String) -> Unit
        ): DatePickerDialog {
            return DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    onDateSelected(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
}