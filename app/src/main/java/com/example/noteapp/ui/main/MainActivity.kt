package com.example.noteapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.data.model.Note
import com.example.noteapp.ui.note.NoteActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        fab = findViewById(R.id.fab)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }
        })
        val mainRecycler = findViewById<RecyclerView>(R.id.mainRecycler)
        mainRecycler.adapter = adapter

        viewModel.viewState().observe(this,
            { t ->
                t?.let { adapter.notes = it.notes }
            })

        fab.setOnClickListener { openNoteScreen(null) }
    }

    private fun openNoteScreen(note: Note?) {
        val intent = NoteActivity.getStartIntent(this, note)
        startActivity(intent)
    }
}