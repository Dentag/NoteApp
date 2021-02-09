package com.example.noteapp.data

import androidx.lifecycle.LiveData
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import com.example.noteapp.data.model.User
import kotlinx.coroutines.channels.ReceiveChannel

interface RemoteDataProvider {
    suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult>

    suspend fun getNoteById(id: String): Note

    suspend fun saveNote(note: Note): Note

    suspend fun getCurrentUser(): User?

    suspend fun deleteNote(noteId: String): Note?
}