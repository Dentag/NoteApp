package com.example.noteapp.ui.note

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.noteapp.data.Repository
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import com.example.noteapp.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class NoteViewModel(val repository: Repository) :
    BaseViewModel<NoteViewState.Data>() {

    private val currentNote: Note?
        get() = getViewState().poll()?.note

    fun saveChanges(note: Note) {
        setData(NoteViewState.Data(note = note))
    }

    fun loadNote(noteId: String) {
        launch {
            try {
                setData(NoteViewState.Data(note = repository.getNoteById(noteId)))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun deleteNote() {
        launch {
            try {
                currentNote?.let {
                    repository.deleteNote(it.id)
                    setData(NoteViewState.Data(isDeleted = true))
                }
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    override fun onCleared() {
        launch {
            currentNote?.let { repository.saveNote(it) }
            super.onCleared()
        }
    }
}