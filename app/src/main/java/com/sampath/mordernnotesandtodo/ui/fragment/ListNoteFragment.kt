package com.sampath.mordernnotesandtodo.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import com.sampath.mordernnotesandtodo.databinding.FragmentListNoteBinding
import com.sampath.mordernnotesandtodo.ui.adapters.ListAdapter
import com.sampath.mordernnotesandtodo.ui.viewModel.NotesViewModel
import com.sampath.mordernnotesandtodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ListNoteFragment : Fragment(R.layout.fragment_list_note) {

    @Inject
    lateinit var adapter: ListAdapter
    private val mNoteViewModel: NotesViewModel by viewModels()
    private var arrNotes = ArrayList<UserNotes>()
    private val binding by viewBinding(FragmentListNoteBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNoteViewModel.readAllNotes.collect{note ->
                if (note.isEmpty()) {
                    binding.noNoteView.visibility = View.VISIBLE
                    binding.searchButton.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.noNoteView.visibility = View.GONE
                    binding.searchButton.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.setData(note)
                    arrNotes = note as ArrayList<UserNotes>
                }
            }
        }



        binding.searchButton.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.searchButton.visibility = View.GONE
            binding.cancelSearch.visibility = View.VISIBLE
            binding.searchView.onActionViewExpanded()

        }

        binding.cancelSearch.setOnClickListener {
            binding.cancelSearch.visibility = View.GONE
            binding.searchButton.visibility = View.VISIBLE
            binding.searchView.onActionViewCollapsed()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempArr = ArrayList<UserNotes>()

                for (arr in arrNotes) {
                    if (arr.notesTitle.lowercase(Locale.getDefault()).trim()
                            .contains(newText.toString()) || arr.notesDescription.lowercase(
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