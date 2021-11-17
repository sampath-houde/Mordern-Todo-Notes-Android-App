package com.example.mordernnotesapp.fragments.Note.AddNoteFragment.AddNoteFragment.ListNoteFragment

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.fragment.Notes.AddOrUpdateNoteFragment.Convertors
import com.example.mordernnotesandtodo.fragment.viewPager.ViewPagerFragmentDirections
import com.example.mordernnotesandtodo.model.UserNotes
import kotlinx.android.synthetic.main.single_note_view.view.*
import kotlinx.coroutines.*

class ListAdapter(context: Context) : RecyclerView.Adapter<ListAdapter.NoteViewHolder>() {

    private var notesList = emptyList<UserNotes>()


    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_note_view, parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.itemView.titleNote.text = currentNote.notesTitle
        holder.itemView.noteDate.text = currentNote.date
        if(TextUtils.isEmpty(currentNote.notesDescription)) {
            holder.itemView.descriptionNote.visibility = View.GONE
        } else {
            holder.itemView.descriptionNote.text = currentNote.notesDescription.trim()
        }

        if(currentNote.imagePath != null) {
            val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

            defaultScope.launch {
                Glide.with(holder.itemView).load(currentNote.imagePath)
                        .also {
                            withContext(Dispatchers.Main) {
                                it.into(holder.itemView.noteImage)
                                holder.itemView.noteImage.visibility = View.VISIBLE
                            }
                        }
            }
        }

        if(currentNote.audio != null) {
            holder.itemView.audio.visibility = View.VISIBLE
        }

        holder.itemView.cardBackground.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToAddNewNoteFragment(currentNote)
            holder.itemView.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun setData(notes: List<UserNotes>){
        this.notesList = notes
        notifyDataSetChanged()
    }
}