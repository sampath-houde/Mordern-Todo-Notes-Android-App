package com.example.mordernnotesapp.fragments.Note.AddNoteFragment.AddNoteFragment.ListNoteFragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import kotlinx.android.synthetic.main.fragment_list_note.view.*
import java.util.*
import kotlin.collections.ArrayList

class ListNoteFragment : Fragment() {

    private lateinit var mNoteViewModel: NotesViewModel

    private var arrNotes = ArrayList<UserNotes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_note, container, false)

        val adapter = ListAdapter(requireContext())
        val recyclerView = view.recyclerView
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        mNoteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        Handler().postDelayed({
            mNoteViewModel.readAllNotes.observe(viewLifecycleOwner, Observer { note->
                if(note.size == 0) {
                    view.noNoteView.visibility = View.VISIBLE
                    view.search_button.visibility = View.GONE
                    view.recyclerView.visibility = View.GONE
                } else {
                    view.noNoteView.visibility = View.GONE
                    view.search_button.visibility = View.VISIBLE
                    view.recyclerView.visibility = View.VISIBLE
                    adapter.setData(note)
                    arrNotes = note as ArrayList<UserNotes>
                }
            })
        }, 300)


        view.search_button.setOnClickListener {
            view.searchView.visibility = View.VISIBLE
            view.searchView.onActionViewExpanded()
        }

        view.searchView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                view.searchView.visibility = View.GONE
                return true
            }

        })

        view.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempArr = ArrayList<UserNotes>()

                for(arr in arrNotes){
                    if(arr.notesTitle.toLowerCase(Locale.getDefault()).trim().contains(newText.toString()) || arr.notesDescription.toLowerCase(
                            Locale.getDefault()).trim().contains(newText.toString())) {
                        tempArr.add(arr)
                    }
                }

                adapter.setData(tempArr)
                adapter.notifyDataSetChanged()

                return true
            }

        })

        view.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_addNewNoteFragment)
        }

        return view
    }
}