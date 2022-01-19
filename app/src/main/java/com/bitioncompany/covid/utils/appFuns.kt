package com.bitioncompany.covid.utils

import android.widget.DatePicker
import java.lang.StringBuilder
import java.util.*

fun DatePicker.getDate(): String {
    val calendar = Calendar.getInstance()
    calendar.set(dayOfMonth, month, year)

    var date = ""

    date = if (month + 1 < 10)
        StringBuilder().append(dayOfMonth).append(".").append(0).append(month + 1).append(".").append(year).toString()
    else StringBuilder().append(dayOfMonth).append(".").append(month + 1).append(".").append(year).toString()

    return date
}