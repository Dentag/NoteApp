package com.example.noteapp.ui.note

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import com.example.noteapp.R
import com.example.noteapp.data.model.Color
import com.example.noteapp.data.model.Note
import com.example.noteapp.databinding.ActivityNoteBinding
import com.example.noteapp.extensions.format
import com.example.noteapp.extensions.getColorInt
import com.example.noteapp.ui.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<NoteViewState.Data, NoteViewState>() {

    override val viewModel: NoteViewModel by viewModel()
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }
    private var note: Note? = null
    private var color: Color = Color.RED

    companion object {
        private val EXTRA_NOTE = "${NoteActivity::class.java.name} extra.NOTE_ID"

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.menu_note, menu).let { true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        setSupportActionBar(ui.toolbar)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let { id ->
            viewModel.loadNote(id)
        } ?: run { supportActionBar?.title = getString(R.string.new_note_title) }

        ui.colorPicker.onColorClickListener = {
            color = it
            setToolbarColor(it)
            triggerSaveNote()
        }

        setEditListener()
    }

    private fun initView() {
        note?.run {
            removeEditListener()
            if (title != ui.titleEt.text.toString()) {
                ui.titleEt.setText(title)
            }
            if (note != ui.bodyEt.text.toString()) {
                ui.bodyEt.setText(note)
            }
            setEditListener()
            supportActionBar?.title = lastChanged.format()
            setToolbarColor(color)
        }
    }

    private fun setToolbarColor(color: Color) {
        ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun togglePalette() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            ui.colorPicker.open()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_dialog_message)
            .setNegativeButton(R.string.cancel_button_title) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok_btn_title) { _, _ -> viewModel.deleteNote() }
            .show()
    }

    private fun triggerSaveNote() {
        ui.titleEt.text?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                note = note?.copy(
                    title = ui.titleEt.text.toString(),
                    note = ui.bodyEt.text.toString(),
                    color = color,
                    lastChanged = Date()
                )
                    ?: createNewNote()

                note?.let { viewModel.saveChanges(it) }
            }, SAVE_DELAY)
        }
    }

    private fun setEditListener() {
        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    private fun removeEditListener() {
        ui.titleEt.removeTextChangedListener(textChangeListener)
        ui.bodyEt.removeTextChangedListener(textChangeListener)
    }

    private fun createNewNote(): Note = Note(
        id = UUID.randomUUID().toString(),
        title = ui.titleEt.text.toString(),
        note = ui.bodyEt.text.toString(),
        color = Color.BLUE
    )

    override fun renderData(data: NoteViewState.Data) {
        if (data.isDeleted) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }
}