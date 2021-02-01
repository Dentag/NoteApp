package com.example.noteapp.ui.main

import androidx.lifecycle.Observer
import com.example.noteapp.data.Repository
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import com.example.noteapp.ui.base.BaseViewModel

class MainViewModel(private val repository: Repository = Repository) :
    BaseViewModel<List<Note>?, MainViewState>() {

    private val notesObserver = object : Observer<NoteResult> {
        override fun onChanged(result: NoteResult?) {
            if (result == null) return

            when (result) {
                is NoteResult.Success<*> -> {
                    viewStateLiveData.value = MainViewState(notes = result.data as? List<Note>)
                }
                is NoteResult.Error -> {
                    viewStateLiveData.value = MainViewState(error = result.error)
                }
            }
        }
    }

    private val repositoryNotes = repository.getNotes()

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}