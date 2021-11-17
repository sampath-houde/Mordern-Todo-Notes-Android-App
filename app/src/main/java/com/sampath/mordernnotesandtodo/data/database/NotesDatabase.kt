package com.sampath.mordernnotesandtodo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sampath.mordernnotesandtodo.data.dao.NotesDao
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import com.sampath.mordernnotesandtodo.utils.Convertors

@TypeConverters(Convertors::class)
@Database(entities = [UserNotes::class], version = 1, exportSchema = false )
abstract class NotesDatabase: RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}