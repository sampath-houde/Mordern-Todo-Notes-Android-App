package com.example.mordernnotesandtodo.repository

import androidx.lifecycle.LiveData
import com.example.mordernnotesandtodo.data.NotesDao
import com.example.mordernnotesandtodo.model.UserNotes

class NotesRepository(private val userDao: NotesDao) {

    val readAllNotes: LiveData<List<UserNotes>> = userDao.readAllNote()

    suspend fun addNote(note: UserNotes) {
        userDao.addNote(note)
    }

    suspend fun updateNote(note: UserNotes) {
        userDao.updateNote(note)
    }

    suspend fun deleteNote(note: UserNotes) {
        userDao.deleteNote(note)
    }
}