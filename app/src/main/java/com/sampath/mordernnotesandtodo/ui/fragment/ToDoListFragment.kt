package com.sampath.mordernnotesandtodo.ui.fragment

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserTodo
import com.sampath.mordernnotesandtodo.databinding.FragmentToDoListBinding
import com.sampath.mordernnotesandtodo.ui.adapters.TodoListAdapter
import com.sampath.mordernnotesandtodo.ui.viewModel.TodoViewModel
import com.sampath.mordernnotesandtodo.utils.TASKS
import com.sampath.mordernnotesandtodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ToDoListFragment : Fragment(R.layout.fragment_to_do_list) {

    private val mTodoViewModel: TodoViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>

    @Inject @Named("defaultCoroutineScope")
    lateinit var defaultScope: CoroutineScope

    @Inject @Named("mainCoroutineScope")
    lateinit var mainScope :CoroutineScope


    private lateinit var adapter: TodoListAdapter


    private lateinit var minDate: PrimeCalendar
    private lateinit var maxDate: PrimeCalendar
    private lateinit var timePicker: SnapTimePickerDialog
    private val binding by viewBinding(FragmentToDoListBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = TodoListAdapter(requireContext()) {
            when(it.first) {
                TASKS.UPDATE -> mTodoViewModel.updateTodo(it.second)
                TASKS.DELETE -> mTodoViewModel.deleteTodo(it.second)
            }
        }

        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mTodoViewModel.readAllTodo.observe(viewLifecycleOwner) { todoList ->

            Handler().postDelayed({                                 // Handler used to show the animation of animation

                if (todoList.isEmpty()) {
                    binding.noNoteView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE

                } else {
                    binding.noNoteView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.setData(todoList)
                }
            }, 300)

        }

        bottomSheet()

    }

    private fun bottomSheet() {
        val bottomSheet: RelativeLayout = binding.LayoutBottom
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.addTodo.setOnClickListener {
            binding.addTodo.visibility = View.INVISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.addTodo.visibility = View.VISIBLE
                    binding.todoTask.setText("")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        binding.doneButton.setOnClickListener {
            defaultScope.launch {
                insertTodoToDatabase()
            }
        }

//        binding.reminderButton.setOnClickListener {
//            //setDateTimePicker()
//        }

    }

    private fun insertTodoToDatabase() {
        val id = 0
        val todoTaskIncomplete = binding.todoTask.text.toString().trim()

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

    private fun setDateTimePicker() {

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
