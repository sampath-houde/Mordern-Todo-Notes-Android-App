package com.sampath.mordernnotesandtodo.data.repository

import androidx.lifecycle.LiveData
import com.sampath.mordernnotesandtodo.data.dao.NotesDao
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class NotesRepository @Inject constructor(@Named("notesDao") private val notesDao: NotesDao) {

    fun getAllNotes() = notesDao.readAllNote()

    suspend fun addNote(note: UserNotes) {
        notesDao.addNote(note)
    }

    suspend fun getNote(noteId: Int): UserNotes {
        return notesDao.getNote(noteId)
    }

    suspend fun updateNote(note: UserNotes) {
        notesDao.updateNote(note)
    }

    suspend fun deleteNote(note: UserNotes) {
        notesDao.deleteNote(note)
    }
}