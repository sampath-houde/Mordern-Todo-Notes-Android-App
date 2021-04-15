package com.example.mordernnotesandtodo.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes_table")
data class UserNotes(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val notesTitle: String,
    val notesDescription: String,
    val date: String,
    val imagePath: ByteArray?
) : Parcelable
