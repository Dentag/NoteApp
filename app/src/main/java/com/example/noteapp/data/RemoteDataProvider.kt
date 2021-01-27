package com.example.noteapp.data

import androidx.lifecycle.LiveData
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult

interface RemoteDataProvider {
    fun subscribeToAllNotes(): LiveData<NoteResult>

    fun getNoteById(id: String): LiveData<NoteResult>

    fun saveNote(note: Note): LiveData<NoteResult>
}