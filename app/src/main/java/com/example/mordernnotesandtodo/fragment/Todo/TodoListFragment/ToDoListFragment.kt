package com.example.mordernnotesandtodo.fragment.Todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.fragment.Todo.TodoListFragment.TodoListAdapter
import com.example.mordernnotesandtodo.viewModel.TodoViewModel
import kotlinx.android.synthetic.main.fragment_to_do_list.view.*

class ToDoListFragment : Fragment() {

    private lateinit var mTodoViewModel: TodoViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)


        val adapter = TodoListAdapter()

        val recyclerView = view.recyclerView

        recyclerView.adapter = adapter

        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mTodoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        mTodoViewModel.readAllTodo.observe(viewLifecycleOwner, Observer { todoList->

            adapter.setData(todoList, mTodoViewModel, context)

        })

        view.addTodo.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_bottomTodoFragment)
        }

        return view
    }

}