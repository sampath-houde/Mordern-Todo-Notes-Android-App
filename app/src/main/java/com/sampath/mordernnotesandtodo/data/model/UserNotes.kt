package com.sampath.mordernnotesandtodo.data.model

import android.os.Parcel
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
    val date: String,
    val imagePath: ByteArray?,
    val audio: String?   //Saving Audio Path that's why string is used.
) : Parcelable {
    override fun describeContents(): Int {
        return -1
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

    }
}
