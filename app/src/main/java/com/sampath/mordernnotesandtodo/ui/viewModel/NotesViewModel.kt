package com.sampath.mordernnotesandtodo.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import com.sampath.mordernnotesandtodo.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepo: NotesRepository) : ViewModel() {

    val readAllNotes = notesRepo.getAllNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _readNote = MutableStateFlow<UserNotes?>(null)
    val readNote: StateFlow<UserNotes?> = _readNote

    fun getNote(noteId: Int) {
        viewModelScope.launch (Dispatchers.IO){
            _readNote.value = notesRepo.getNote(noteId)
        }
    }
    fun addNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepo.addNote(note)
        }
    }

    fun updateNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepo.updateNote(note)
        }
    }

    fun deleteNote(note: UserNotes) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepo.deleteNote(note)
        }
    }

}