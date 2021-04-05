package com.example.mordernnotesandtodo.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes_table")
data class UserNotes(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val notesTitle: String,
    val notesDescription: String,
    val date: String
) : Parcelable
