package com.sampath.mordernnotesandtodo.utils

import  java.text.DateFormat
import java.util.*


class FormatDate {
    val date: String

    init {
       val currentTime = Calendar.getInstance().time
        val formattedDate = DateFormat.getDateInstance().format(currentTime)
        date = formattedDate
    }
}