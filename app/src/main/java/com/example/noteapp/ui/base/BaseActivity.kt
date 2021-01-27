package com.example.noteapp.ui.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {

    abstract val viewModel: BaseViewModel<T, S>
    abstract val ui: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        viewModel.getViewState().observe(this, object : Observer<S> {
            override fun onChanged(t: S?) {
                if (t == null) return
                if (t.data != null) renderData(t.data!!)
                if (t.error != null) renderError(t.error)
            }
        })
    }

    protected fun renderError(error: Throwable) {
        if (error.message != null) showError(error.message!!)
    }

    abstract fun renderData(data: T)

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}