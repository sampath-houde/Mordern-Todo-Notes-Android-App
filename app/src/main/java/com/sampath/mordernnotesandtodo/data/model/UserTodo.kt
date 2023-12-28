package com.sampath.mordernnotesandtodo.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "user_todo")
data class UserTodo(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val todoTask: String,
        var todoTaskCompleted: Boolean
) : Parcelable {
        override fun describeContents(): Int {
                return -1
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {

        }
}
