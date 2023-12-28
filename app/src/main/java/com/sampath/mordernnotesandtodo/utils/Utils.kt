package com.sampath.mordernnotesandtodo.utils


object Utils {

    fun startTimer(milliseconds: Long) : String {
        var timerString = ""
        var secondsString = ""

        val hours : Int = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes: Int = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        val seconds: Int = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()

        if(hours > 0) {
            timerString = "$hours:"
        }

        secondsString = if(seconds < 10) {
            "0${seconds}"
        } else {
            "$seconds"
        }

        timerString = "$timerString $minutes : $secondsString"
        return timerString
    }


}