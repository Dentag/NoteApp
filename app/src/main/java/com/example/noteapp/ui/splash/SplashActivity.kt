package com.example.noteapp.ui.splash

import android.os.Handler
import android.os.Looper
import com.example.noteapp.databinding.ActivitySplashBinding
import com.example.noteapp.ui.base.BaseActivity
import com.example.noteapp.ui.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

private const val START_DELAY = 1000L

@ExperimentalCoroutinesApi
class SplashActivity : BaseActivity<Boolean>() {

    override val viewModel: SplashViewModel by viewModel()

    override val ui: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ viewModel.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean) {
        if (data) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }
}