package com.example.noteapp.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.R
import com.example.noteapp.data.model.Color
import com.example.noteapp.data.model.Note
import com.example.noteapp.databinding.ActivityNoteBinding
import com.example.noteapp.extensions.format
import com.example.noteapp.extensions.getColorInt
import com.example.noteapp.ui.base.BaseActivity
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    override val viewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(NoteViewModel::class.java)
    }
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    private var note: Note? = null

    companion object {
        private val EXTRA_NOTE = "${NoteActivity::class.java.name} extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

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
        setContentView(ui.root)
        setSupportActionBar(ui.toolbar)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let { id ->
            viewModel.loadNote(id)
        } ?: let { supportActionBar?.title = getString(R.string.new_note_title) }

        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)

        initView()
    }

    private fun initView() {
        note?.run {
            supportActionBar?.title = lastChanged.format()

            ui.titleEt.setText(title)
            ui.bodyEt.setText(note)

            ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
        }

        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun triggerSaveNote() {
        ui.titleEt.text?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                note = note?.copy(
                    title = ui.titleEt.text.toString(),
                    note = ui.bodyEt.text.toString(),
                    lastChanged = Date()
                )
                    ?: createNewNote()

                if (note != null) viewModel.saveChanges(note!!)
            }, SAVE_DELAY)
        }
    }

    private fun createNewNote(): Note = Note(
        id = UUID.randomUUID().toString(),
        title = ui.titleEt.text.toString(),
        note = ui.bodyEt.text.toString(),
        color = Color.BLUE
    )

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }
}