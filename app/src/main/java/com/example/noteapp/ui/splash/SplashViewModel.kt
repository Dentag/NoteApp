package com.example.noteapp.ui.splash

import com.example.noteapp.data.Repository
import com.example.noteapp.data.errors.NoAuthException
import com.example.noteapp.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository) :
    BaseViewModel<Boolean>() {

    @ExperimentalCoroutinesApi
    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}
