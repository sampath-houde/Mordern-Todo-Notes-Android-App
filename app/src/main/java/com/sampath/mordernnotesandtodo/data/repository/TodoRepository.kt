package com.sampath.mordernnotesandtodo.data.repository

import androidx.lifecycle.LiveData
import com.sampath.mordernnotesandtodo.data.dao.TodoDao
import com.sampath.mordernnotesandtodo.data.model.UserTodo
import javax.inject.Inject
import javax.inject.Named

class TodoRepository @Inject constructor(@Named("todoDao")private val todoDao: TodoDao) {

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