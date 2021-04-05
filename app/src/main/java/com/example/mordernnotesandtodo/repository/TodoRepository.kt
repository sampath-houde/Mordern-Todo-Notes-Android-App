package com.example.mordernnotesandtodo.repository

import androidx.lifecycle.LiveData
import com.example.mordernnotesandtodo.data.TodoDao
import com.example.mordernnotesandtodo.model.UserTodo

class TodoRepository(private val todoDao: TodoDao) {

    val readAllTodo: LiveData<List<UserTodo>> = todoDao.readAllTodo()

    suspend fun addTodo(todo: UserTodo) {
        todoDao.addTodo(todo)
    }

    suspend fun updateTodo(todo: UserTodo) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: UserTodo) {
        todoDao.deleteTodo(todo)
    }
}