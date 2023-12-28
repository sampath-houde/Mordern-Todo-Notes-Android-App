package com.sampath.mordernnotesandtodo.data.dao

import androidx.room.*
import androidx.room.Dao
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: UserNotes)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun readAllNote(): Flow<List<UserNotes>>

    @Update
    suspend fun updateNote(note: UserNotes)

    @Delete
    suspend fun deleteNote(note: UserNotes)

    @Query("SELECT * FROM notes_table WHERE id = :noteId")
    fun getNote(noteId: Int): UserNotes
}