package com.example.noteapp.ui.main

import com.example.noteapp.data.model.Note
import com.example.noteapp.ui.base.BaseViewState

class MainViewState(notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)