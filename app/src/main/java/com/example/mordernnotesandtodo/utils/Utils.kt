package com.example.mordernnotesandtodo.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.databinding.RecordAudioDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

object Utils {

    fun startTimer(milliseconds: Long) : String {
        var timerString : String = ""
        var secondsString : String = ""

        val hours : Int = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes: Int = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        val seconds: Int = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()

        if(hours > 0) {
            timerString = "$hours:"
        }

        if(seconds < 10) {
            secondsString = "0${seconds}"
        } else {
            secondsString = "$seconds"
        }

        timerString = "${timerString} ${minutes} : ${secondsString}"
        return timerString
    }
}