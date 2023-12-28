package com.sampath.mordernnotesandtodo.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sampath.mordernnotesandtodo.data.database.TodoDatabase
import com.sampath.mordernnotesandtodo.data.model.UserTodo
import com.sampath.mordernnotesandtodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val todoRepo: TodoRepository): ViewModel() {

     val readAllTodo: LiveData<List<UserTodo>> = todoRepo.readAllTodo

    fun addTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.addTodo(todo)
        }
    }

    fun updateTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.deleteTodo(todo)
        }
    }
}