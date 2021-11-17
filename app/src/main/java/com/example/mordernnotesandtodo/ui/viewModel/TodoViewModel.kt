package com.example.mordernnotesandtodo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mordernnotesandtodo.data.TodoDatabase
import com.example.mordernnotesandtodo.model.UserTodo
import com.example.mordernnotesandtodo.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application): AndroidViewModel(application) {

     val readAllTodo: LiveData<List<UserTodo>>
    private val todoRepository: TodoRepository

    init {
        val todoDao = TodoDatabase.getDatabase(application).todoDao()
        todoRepository = TodoRepository(todoDao)
        readAllTodo = todoRepository.readAllTodo
    }

    fun addTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.addTodo(todo)
        }
    }

    fun updateTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: UserTodo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteTodo(todo)
        }
    }
}