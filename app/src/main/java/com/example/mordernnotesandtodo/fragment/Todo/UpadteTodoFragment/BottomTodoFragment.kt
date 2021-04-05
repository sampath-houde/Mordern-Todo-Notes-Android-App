package com.example.mordernnotesandtodo.fragment.Todo.UpadteTodoFragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.model.UserTodo
import com.example.mordernnotesandtodo.viewModel.TodoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_todo.*
import kotlinx.android.synthetic.main.fragment_bottom_todo.view.*

class BottomTodoFragment : BottomSheetDialogFragment() {

    private lateinit var mTodoViewModel: TodoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_todo, container, false)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        mTodoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        view.doneButton.setOnClickListener {
            insertTodoToDatabase()
        }

        return view
    }

    private fun insertTodoToDatabase() {
        val id = 0
        val todoTaskIncomplete = todoTask.text.toString().trim()

        if(!TextUtils.isEmpty(todoTaskIncomplete)) {
            val todo = UserTodo(id, todoTaskIncomplete, false)
            mTodoViewModel.addTodo(todo)
            Toast.makeText(requireContext(), "Task Added", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Enter Task", Toast.LENGTH_SHORT).show()
        }
    }


}