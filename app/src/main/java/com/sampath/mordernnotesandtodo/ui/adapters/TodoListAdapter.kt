package com.sampath.mordernnotesandtodo.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserTodo
import com.sampath.mordernnotesandtodo.databinding.SingleTodoViewBinding
import com.sampath.mordernnotesandtodo.utils.TASKS
import timber.log.Timber


class TodoListAdapter (val context: Context, private val todoOperation: (Pair<TASKS, UserTodo>)-> Unit,
) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    private var todoList = emptyList<UserTodo>()

    fun setData(todo: List<UserTodo>) {
        todoList = todo
        notifyDataSetChanged()
    }

    class TodoViewHolder(
        private val binding: SingleTodoViewBinding,
        private val context: Context,
        private val todoOperation: (Pair<TASKS, UserTodo>) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(currentTodo: UserTodo) {
            binding.checkBox.isChecked = currentTodo.todoTaskCompleted

            if (!currentTodo.todoTaskCompleted) {
                binding.titleTodo.text = currentTodo.todoTask
                binding.titleTodo.paintFlags = 0
                binding.titleTodo.setTextColor(context.getColor(R.color.titleColor))
                binding.checkBox.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.titleColor))
                binding.cardBackground.setCardBackgroundColor(context.getColor(R.color.SingleTodobackgroundIncomplete))
            } else {
                binding.titleTodo.text = currentTodo.todoTask
                binding.titleTodo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.titleTodo.setTextColor(context.getColor(R.color.textHintColor))
                binding.checkBox.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.textHintColor))
                binding.cardBackground.setCardBackgroundColor(context.getColor(R.color.SingleTodobackgroundComplete))
            }

            binding.deleteButton.setOnClickListener {
                createDeleteTodoAlertDialog(currentTodo, context, todoOperation)
            }

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentTodo.todoTaskCompleted = true
                    todoOperation(Pair(TASKS.UPDATE, currentTodo))
                } else {
                    currentTodo.todoTaskCompleted = false
                    todoOperation(Pair(TASKS.UPDATE, currentTodo))
                }
            }
        }

        private fun createDeleteTodoAlertDialog(
            currentTodo: UserTodo,
            context: Context,
            todoOperation: (Pair<TASKS, UserTodo>) -> Unit
        ) {
            val li = LayoutInflater.from(context)
            val view = li.inflate(R.layout.delete_note_alert_dialog, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(view)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val deleteTitle = view.findViewById<TextView>(R.id.deleteTitle)
            val deleteDesc = view.findViewById<TextView>(R.id.deleteNoteDesc)
            val delete = view.findViewById<TextView>(R.id.deleteButton)
            val cancel = view.findViewById<TextView>(R.id.cancelButton)
            deleteTitle.text = context.resources.getText(R.string.deleteTodo)
            deleteDesc.text = context.resources.getText(R.string.deleteTodoDesc)

            delete.setOnClickListener {
                todoOperation(Pair(TASKS.DELETE, currentTodo))
                dialog.dismiss()
            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = SingleTodoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding, context, todoOperation)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todoList[position]
        holder.bind(currentTodo)
    }


    override fun getItemCount(): Int {
        Timber.d("Todo List ${todoList.size}")
        return todoList.size
    }

}