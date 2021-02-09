package com.example.noteapp.ui.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import com.example.noteapp.data.Repository
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import com.example.noteapp.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Modifier.PROTECTED

@ExperimentalCoroutinesApi
class MainViewModel(private val repository: Repository) :
    BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy { runBlocking { repository.getNotes() } }

    init {
        launch {
            notesChannel.consumeEach { result ->
                when (result) {
                    is NoteResult.Success<*> -> setData(result.data as? List<Note>)
                    is NoteResult.Error -> setError(result.error)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @VisibleForTesting(otherwise = PROTECTED)
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }
}