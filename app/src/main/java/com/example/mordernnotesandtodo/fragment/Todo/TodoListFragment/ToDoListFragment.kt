package com.example.mordernnotesandtodo.fragment.Todo

import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.fragment.Todo.TodoListFragment.TodoListAdapter
import com.example.mordernnotesandtodo.model.UserTodo
import com.example.mordernnotesandtodo.viewModel.TodoViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_bottom_todo.*
import kotlinx.android.synthetic.main.fragment_to_do_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.DateFormat

class ToDoListFragment : Fragment() {

    private lateinit var mTodoViewModel: TodoViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var minDate: PrimeCalendar
    private lateinit var maxDate: PrimeCalendar
    private lateinit var timePicker: SnapTimePickerDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)

        mTodoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        val adapter = TodoListAdapter(mTodoViewModel, context)

        val recyclerView = view.recyclerView

        recyclerView.adapter = adapter

        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mTodoViewModel.readAllTodo.observe(viewLifecycleOwner, Observer { todoList ->

            Handler().postDelayed({                                 // Handler used to show the animation of animation

                if (todoList.size == 0) {
                    view.noNoteView.visibility = View.VISIBLE
                    view.recyclerView.visibility = View.GONE

                } else {
                    view.noNoteView.visibility = View.GONE
                    view.recyclerView.visibility = View.VISIBLE
                    adapter.setData(todoList)
                }
            }, 300)

        })

        bottomSheet(view)


        return view
    }

    private fun bottomSheet(view: View) {
        val bottomSheet: RelativeLayout = view.LayoutBottom
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        view.addTodo.setOnClickListener {
            view.addTodo.visibility = View.INVISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    view.addTodo.visibility = View.VISIBLE
                    view.todoTask.setText("")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        view.doneButton.setOnClickListener {
            defaultScope.launch {
                insertTodoToDatabase()
            }
        }

        view.reminderButton.setOnClickListener {
            setDateTimePicker(view)
        }

    }

    private fun insertTodoToDatabase() {
        val id = 0
        val todoTaskIncomplete = todoTask.text.toString().trim()

        if (!TextUtils.isEmpty(todoTaskIncomplete)) {
            val todo = UserTodo(id, todoTaskIncomplete, false)
            mTodoViewModel.addTodo(todo)
            mainScope.launch {
                Toast.makeText(requireContext(), "Task Added", Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            findNavController().navigateUp()
        } else {
            mainScope.launch {
                Toast.makeText(requireContext(), "Enter Task", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setDateTimePicker(view: View) {

        setCalendarType()

        val datePicker = PrimeDatePicker.bottomSheetWith(CivilCalendar())
            .pickSingleDay(object : SingleDayPickCallback {
                override fun onSingleDayPicked(singleDay: PrimeCalendar?) {
                    Log.d("Calendar", singleDay!!.getTime().toString())
                    timePicker.show(parentFragmentManager, "sampath")
                    timePicker.setListener(object : SnapTimePickerDialog.Listener {
                        override fun onTimePicked(hour: Int, minute: Int) {

                            setAlarm(singleDay)
                        }

                    })
                }

            }).minPossibleDate(minDate)
            .maxPossibleDate(maxDate)

        datePicker.build().show(parentFragmentManager, "ds")

    }

    private fun setAlarm(singleDay: PrimeCalendar) {
        Log.d("Time", singleDay.getTime().toString())
    }

    private fun setCalendarType() {
        minDate = CalendarFactory.newInstance(CalendarType.CIVIL)
        maxDate = CalendarFactory.newInstance(CalendarType.CIVIL)
        maxDate[Calendar.MONTH] = 11

        timePicker = SnapTimePickerDialog.Builder()
            .setTitle(R.string.selectTime)
            .setPrefix(R.string.hour)
            .setSuffix(R.string.minute)
            .setThemeColor(R.color.SingleTodobackgroundComplete)
            .build()
    }


}
