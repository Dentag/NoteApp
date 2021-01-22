package com.example.noteapp.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.noteapp.R
import com.example.noteapp.data.model.Color
import com.example.noteapp.data.model.Note
import com.example.noteapp.extensions.DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getStartIntent(context: Context, note: Note?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, note)
            return intent
        }
    }

    private var note: Note? = null
    private lateinit var titleEt: EditText
    private lateinit var bodyEt: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var viewModel: NoteViewModel

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            triggerSaveNote()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not used
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note)

        titleEt = findViewById(R.id.titleEt)
        bodyEt = findViewById(R.id.bodyEt)
        toolbar = findViewById(R.id.toolbar)
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)


        note = intent.getParcelableExtra(EXTRA_NOTE)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (note != null) {
            SimpleDateFormat(
                DATE_TIME_FORMAT,
                Locale.getDefault()
            ).format(note!!.lastChanged)
        } else {
            getString(R.string.new_note_title)
        }

        initView()
    }

    private fun initView() {
        titleEt.setText(note?.title ?: "")
        bodyEt.setText(note?.note ?: "")
        val color = when (note?.color) {
            Color.WHITE -> R.color.color_white
            Color.VIOLET -> R.color.color_violet
            Color.YELLOW -> R.color.color_yellow
            Color.RED -> R.color.color_red
            Color.PINK -> R.color.color_pink
            Color.GREEN -> R.color.color_green
            Color.BLUE -> R.color.color_blue
            else -> R.color.white
        }

        toolbar.setBackgroundColor(resources.getColor(color))
        titleEt.addTextChangedListener(textChangeListener)
        bodyEt.addTextChangedListener(textChangeListener)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun triggerSaveNote() {
        if (titleEt.text.length < 3) return

        Handler().postDelayed(object : Runnable {
            override fun run() {
                note = note?.copy(
                    title = titleEt.text.toString(),
                    note = bodyEt.text.toString(),
                    lastChanged = Date()
                )
                    ?: createNewNote()

                if (note != null) viewModel.saveChanges(note!!)
            }

        }, SAVE_DELAY)
    }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        titleEt.text.toString(),
        bodyEt.text.toString()
    )
}