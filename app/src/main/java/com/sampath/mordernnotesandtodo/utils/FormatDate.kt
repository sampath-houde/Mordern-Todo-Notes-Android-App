package com.sampath.mordernnotesandtodo.utils

import  java.text.DateFormat
import java.util.*


class FormatDate {
    val date: String

    init {
       val currentTime = Calendar.getInstance().time
        val formattedDate = DateFormat.getDateTimeInstance().format(currentTime).split("-")
        date = formattedDate[1] + " " + formattedDate[0] + ", " + formattedDate[2].subSequence(0,4)
    }
}