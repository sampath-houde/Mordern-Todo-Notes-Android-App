package com.example.mordernnotesandtodo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordernnotesandtodo.data.NotesDatabase
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    val readAllNotes: LiveData<List<UserNotes>>
    private val notesRepository: NotesRepository

    init {
        val notesDao = NotesDatabase.getDatabase(application).notesDao()
        notesRepository = NotesRepository(notesDao)
        readAllNotes = notesRepository.readAllNotes
    }

    fun addNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.addNote(note)
        }
    }

    fun updateNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.updateNote(note)
        }
    }

    fun deleteNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.deleteNote(note)
        }
    }

}