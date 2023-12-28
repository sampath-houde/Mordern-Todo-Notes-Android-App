package com.sampath.mordernnotesandtodo.ui.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import com.sampath.mordernnotesandtodo.databinding.SingleNoteViewBinding
import com.sampath.mordernnotesandtodo.ui.fragment.ViewPagerFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.*
import javax.inject.Inject

class ListAdapter @Inject constructor(
    @ActivityContext private val context: Context
) : RecyclerView.Adapter<ListAdapter.NoteViewHolder>() {

    private var notesList = emptyList<UserNotes>()
    class NoteViewHolder(private val binding: SingleNoteViewBinding, val context: Context): RecyclerView.ViewHolder(binding.root)  {
        fun bind(currentNote: UserNotes) {
            binding.titleNote.text = currentNote.notesTitle
            binding.noteDate.text = currentNote.date
            if(TextUtils.isEmpty(currentNote.notesDescription)) {
                binding.descriptionNote.visibility = View.GONE
            } else {
                binding.descriptionNote.text = currentNote.notesDescription.trim()
            }

            if(currentNote.imagePath != null) {
                val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

                defaultScope.launch {
                    Glide.with(context).load(currentNote.imagePath)
                        .also {
                            withContext(Dispatchers.Main) {
                                it.into(binding.noteImage)
                                binding.noteImage.visibility = View.VISIBLE
                            }
                        }
                }
            }

            if(currentNote.audio != null) {
                binding.audio.visibility = View.VISIBLE
            }

            binding.cardBackground.setOnClickListener {
                val action = ViewPagerFragmentDirections.actionViewPagerFragmentToAddNewNoteFragment(currentNote.id)
                binding.root.findNavController().navigate(action)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = SingleNoteViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.bind(currentNote)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun setData(notes: List<UserNotes>){
        this.notesList = notes
        notifyDataSetChanged()
    }
}