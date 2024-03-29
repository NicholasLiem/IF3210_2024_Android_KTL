package com.ktl.bondoman.ui

import NetworkReceiver
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progressBar = findViewById(R.id.progressBar)

        progressBar.max = 1000;
        val curProgress = 1000;

        ObjectAnimator.ofInt(progressBar, "progress", curProgress)
            .setDuration(1000)
            .start()

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

}