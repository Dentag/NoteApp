package com.example.noteapp.ui.note

import com.example.noteapp.data.model.Note
import com.example.noteapp.ui.base.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) :
    BaseViewState<Note?>(note, error)