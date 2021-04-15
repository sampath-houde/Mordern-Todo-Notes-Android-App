package com.example.mordernnotesandtodo.fragment.Todo.TodoListFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


 class MyAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Alarm Bell", "Alarm fired")
        Toast.makeText(context, "Fired", Toast.LENGTH_SHORT).show()
    }

}