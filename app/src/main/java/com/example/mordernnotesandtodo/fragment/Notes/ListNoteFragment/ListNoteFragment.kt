package com.example.mordernnotesapp.fragments.Note.AddNoteFragment.AddNoteFragment.ListNoteFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.databinding.FragmentListNoteBinding
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import java.util.*
import kotlin.collections.ArrayList

class ListNoteFragment : Fragment() {


    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var liveDataNotes: LiveData<List<UserNotes>>
    private lateinit var binding: FragmentListNoteBinding
    private var arrNotes = ArrayList<UserNotes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_note, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentListNoteBinding.bind(view)

        val adapter = ListAdapter(requireContext())
        val recyclerView = binding.recyclerView
        mNoteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        liveDataNotes = mNoteViewModel.readAllNotes

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)



        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner, Observer { note ->

            if(note.size == 0) {
                binding.recyclerView.visibility = View.INVISIBLE
                binding.noNoteView.visibility = View.VISIBLE
                binding.searchButton.visibility = View.INVISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.noNoteView.visibility = View.INVISIBLE
                binding.searchButton.visibility = View.VISIBLE
                adapter.setData(note)
                arrNotes = note as ArrayList<UserNotes>
            }


        })


        binding.searchButton.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.onActionViewExpanded()
        }

        binding.searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                binding.searchView.visibility = View.GONE
                return true
            }

        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempArr = ArrayList<UserNotes>()

                for (arr in arrNotes) {
                    if (arr.notesTitle.toLowerCase(Locale.getDefault()).trim()
                            .contains(newText.toString()) || arr.notesDescription.toLowerCase(
                            Locale.getDefault()
                        ).trim().contains(newText.toString())
                    ) {
                        tempArr.add(arr)
                    }
                }

                adapter.setData(tempArr)
                adapter.notifyDataSetChanged()

                return true
            }

        })

        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_addNewNoteFragment)
        }
    }
}