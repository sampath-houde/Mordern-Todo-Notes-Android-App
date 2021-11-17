package com.sampath.mordernnotesandtodo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.sampath.mordernnotesandtodo.data.model.UserNotes

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: UserNotes)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun readAllNote(): LiveData<List<UserNotes>>

    @Update
    suspend fun updateNote(note: UserNotes)

    @Delete
    suspend fun deleteNote(note: UserNotes)
}