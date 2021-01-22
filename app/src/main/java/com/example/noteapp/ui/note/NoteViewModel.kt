package com.example.noteapp.ui.note

import androidx.lifecycle.ViewModel
import com.example.noteapp.data.Repository
import com.example.noteapp.data.model.Note

class NoteViewModel(private val repository: Repository = Repository) : ViewModel() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }
}