package com.example.mordernnotesapp.fragments.Note.AddNoteFragment.AddNoteFragment.ListNoteFragment

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.fragment.viewPager.ViewPagerFragmentDirections
import com.example.mordernnotesandtodo.model.UserNotes
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import kotlinx.android.synthetic.main.fragment_add_note.view.noteDescription
import kotlinx.android.synthetic.main.fragment_add_note.view.noteTitle
import kotlinx.android.synthetic.main.single_note_view.view.*

class ListAdapter(context: Context) : RecyclerView.Adapter<ListAdapter.NoteViewHolder>() {

    private var notesList = emptyList<UserNotes>()

    //private var notesColor: IntArray = context.resources.getIntArray(R.array.notesColor)

    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_note_view, parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.itemView.titleNote.text = currentNote.notesTitle
        //holder.itemView.cardBackground.setCardBackgroundColor(notesColor.random())
        holder.itemView.noteDate.text = currentNote.date
        if(TextUtils.isEmpty(currentNote.notesDescription)) {
            holder.itemView.descriptionNote.visibility = View.GONE
        } else {
            holder.itemView.descriptionNote.text = currentNote.notesDescription.trim()
        }

        holder.itemView.cardBackground.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToUpdateNoteFragment(currentNote)
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