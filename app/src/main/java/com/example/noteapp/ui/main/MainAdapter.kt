package com.example.noteapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.data.model.Color
import com.example.noteapp.data.model.Note
import com.example.noteapp.databinding.ItemNoteBinding
import com.example.noteapp.extensions.getColorInt

interface OnItemClickListener {
    fun onItemClick(note: Note)
}

class MainAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ui: ItemNoteBinding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note) {
            ui.title.text = note.title
            ui.body.text = note.note

            ui.container.setCardBackgroundColor(note.color.getColorInt(itemView.context))
            itemView.setOnClickListener { onItemClickListener.onItemClick(note) }
        }
    }
}