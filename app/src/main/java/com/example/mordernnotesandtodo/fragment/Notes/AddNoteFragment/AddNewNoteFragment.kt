package com.example.mordernnotesandtodo.fragment.Notes.AddNoteFragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_add_note.view.*

class AddNewNoteFragment : Fragment() {

    private val date = FormatDate().date
    private lateinit var mNotesViewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_note, container, false)

        mNotesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        view.dateTime.text = date

        view.save_button.setOnClickListener {
            insertNoteToDatabase()
        }

        view.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }


    private fun insertNoteToDatabase() {
        val id = 0
        val noteTitle = noteTitle.text.toString().trim()
        val noteDescription = noteDescription.text.toString().trim()

        if(inputCheck(noteTitle, noteDescription)){

            val note = UserNotes(id, noteTitle, noteDescription, date)
            mNotesViewModel.addNote(note)
            Toast.makeText(context, "Successfully Added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
        } else {
            Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }

}