package com.sampath.mordernnotesandtodo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sampath.mordernnotesandtodo.data.dao.TodoDao
import com.sampath.mordernnotesandtodo.data.model.UserTodo

@Database(entities = [UserTodo::class], version = 1, exportSchema = false )
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao

}