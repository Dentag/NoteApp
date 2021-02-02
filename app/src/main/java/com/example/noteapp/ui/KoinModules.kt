package com.example.noteapp.ui

import com.example.noteapp.data.FireStoreProvider
import com.example.noteapp.data.RemoteDataProvider
import com.example.noteapp.data.Repository
import com.example.noteapp.ui.main.MainViewModel
import com.example.noteapp.ui.note.NoteViewModel
import com.example.noteapp.ui.splash.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FireStoreProvider(get(), get()) } bind RemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}
val mainModule = module {
    viewModel { MainViewModel(get()) }
}
val noteModule = module {
    viewModel { NoteViewModel(get()) }
}