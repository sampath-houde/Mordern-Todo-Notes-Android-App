package com.example.mordernnotesapp.fragments.Note.AddNoteFragment.AddNoteFragment.UpdateNoteFragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.fragment.Notes.AddNoteFragment.FormatDate
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import kotlinx.android.synthetic.main.fragment_update_note.*
import kotlinx.android.synthetic.main.fragment_update_note.view.*

class UpdateNoteFragment : Fragment() {

    private val date = FormatDate().date

    private val args by navArgs<UpdateNoteFragmentArgs>()

    private lateinit var mNotesViewModel: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_note, container, false)

        mNotesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        view.noteTitle.setText(args.notesData.notesTitle)
        view.noteDescription.setText(args.notesData.notesDescription)
        view.dateTime.setText(args.notesData.date)

        view.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        view.updateButton.setOnClickListener {
            updateNoteToDatabase()
        }

        view.deleteButton.setOnClickListener {
            deleteNoteFromDataBase()
        }

        return view
    }

    private fun updateNoteToDatabase() {
        val noteTitle = noteTitle.text.toString().trim()
        val noteDescription = noteDescription.text.toString().trim()

        if(inputCheck(noteTitle, noteDescription)){

            val note = UserNotes(args.notesData.id, noteTitle, noteDescription, date)
            mNotesViewModel.updateNote(note)
            Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateNoteFragment_to_viewPagerFragment)
        } else {
            Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNoteFromDataBase() {
        mNotesViewModel.deleteNote(args.notesData)
        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateNoteFragment_to_viewPagerFragment)
    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }


}