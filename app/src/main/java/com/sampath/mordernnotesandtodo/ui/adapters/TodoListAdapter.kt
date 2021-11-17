package com.sampath.mordernnotesandtodo.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserTodo
import com.sampath.mordernnotesandtodo.ui.viewModel.TodoViewModel
import kotlinx.android.synthetic.main.single_todo_view.view.*

class TodoListAdapter(
    mTodoViewModel: TodoViewModel,
    context: Context?,
    requireActivity: FragmentActivity
) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    private var todoList = emptyList<UserTodo>()
    private  var mUserTodoViewModel: TodoViewModel = mTodoViewModel
    private  var context: Context = context!!
    private var requireActivity: FragmentActivity

    init {
        this.requireActivity = requireActivity
    }

    fun setData(todo: List<UserTodo>) {
        todoList = todo
        notifyDataSetChanged()

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
            createDeleteTodoAlertDialog(currentTodo)
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

    private fun createDeleteTodoAlertDialog(currentTodo: UserTodo) {
        val view = requireActivity.layoutInflater.inflate(R.layout.delete_note_alert_dialog, null)
        val builder = AlertDialog.Builder(requireActivity)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteTitle = view.findViewById<TextView>(R.id.deleteTitle)
        val deleteDesc = view.findViewById<TextView>(R.id.deleteNoteDesc)
        val delete = view.findViewById<TextView>(R.id.deleteButton)
        val cancel = view.findViewById<TextView>(R.id.cancelButton)
        deleteTitle.setText(context.resources.getText(R.string.deleteTodo))
        deleteDesc.setText(context.resources.getText(R.string.deleteTodoDesc))

        delete.setOnClickListener {
            mUserTodoViewModel.deleteTodo(currentTodo)
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun getItemCount(): Int {
        return todoList.size
    }

}