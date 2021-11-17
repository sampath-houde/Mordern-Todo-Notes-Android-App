package com.example.mordernnotesandtodo.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_todo")
data class UserTodo(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val todoTask: String,
        var todoTaskCompleted: Boolean
) : Parcelable
