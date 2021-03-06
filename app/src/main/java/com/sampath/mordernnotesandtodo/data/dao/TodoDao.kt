package com.sampath.mordernnotesandtodo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.sampath.mordernnotesandtodo.data.model.UserTodo

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTodo(todo: UserTodo)

    @Query("SELECT * FROM user_todo ORDER BY id ASC")
    fun readAllTodo(): LiveData<List<UserTodo>>

    @Update
    suspend fun updateTodo(todo: UserTodo)

    @Delete
    suspend fun deleteTodo(todo: UserTodo)
}