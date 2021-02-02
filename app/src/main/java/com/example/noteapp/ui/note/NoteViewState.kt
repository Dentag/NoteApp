package com.example.noteapp.ui.note

import com.example.noteapp.data.model.Note
import com.example.noteapp.ui.base.BaseViewState

class NoteViewState(data: Data = Data(), error: Throwable? = null) :
    BaseViewState<NoteViewState.Data>(data, error) {
    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}