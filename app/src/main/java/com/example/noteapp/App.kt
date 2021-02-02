package com.example.noteapp

import androidx.multidex.MultiDexApplication
import com.example.noteapp.ui.appModule
import com.example.noteapp.ui.mainModule
import com.example.noteapp.ui.noteModule
import com.example.noteapp.ui.splashModule
import org.koin.core.context.startKoin

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule, splashModule, mainModule, noteModule)
        }
    }
}