package com.example.mordernnotesandtodo.fragment.Todo.TodoListFragment

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.model.UserTodo
import com.example.mordernnotesandtodo.viewModel.TodoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.single_todo_view.view.*

class TodoListAdapter() : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    private var todoList = emptyList<UserTodo>()
    private lateinit var mUserTodoViewModel: TodoViewModel
    private lateinit var context: Context

    fun setData(todo: List<UserTodo>, mTodoViewModel: TodoViewModel, context: Context?) {
        todoList = todo
        notifyDataSetChanged()

        this.context = context!!
        mUserTodoViewModel = mTodoViewModel
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_todo_view, parent, false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todoList[position]

        holder.itemView.checkBox.isChecked = currentTodo.todoTaskCompleted

        if (!currentTodo.todoTaskCompleted) {
            holder.itemView.titleTodo.text = currentTodo.todoTask
            holder.itemView.titleTodo.paintFlags = 0
            holder.itemView.titleTodo.setTextColor(context.getColor(R.color.titleColor))
            holder.itemView.checkBox.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.titleColor))
            holder.itemView.cardBackground.setCardBackgroundColor(context.getColor(R.color.SingleTodobackgroundIncomplete))
        } else {
            holder.itemView.titleTodo.text = currentTodo.todoTask
            holder.itemView.titleTodo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.itemView.titleTodo.setTextColor(context.getColor(R.color.textHintColor))
            holder.itemView.checkBox.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.textHintColor))
            holder.itemView.cardBackground.setCardBackgroundColor(context.getColor(R.color.SingleTodobackgroundComplete))
        }

        holder.itemView.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.deleteTodo))
                    .setNegativeButton(context.getText(R.string.No)) { dialog, which ->

                    }.setPositiveButton(context.getText(R.string.Yes)) { dialog, which ->
                        mUserTodoViewModel.deleteTodo(currentTodo)
                    }.show()
        }

        holder.itemView.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    currentTodo.todoTaskCompleted = true
                    mUserTodoViewModel.updateTodo(currentTodo)
                } else {
                    currentTodo.todoTaskCompleted = false
                    mUserTodoViewModel.updateTodo(currentTodo)
                }
            }

        })

    }


    override fun getItemCount(): Int {
        return todoList.size
    }

}