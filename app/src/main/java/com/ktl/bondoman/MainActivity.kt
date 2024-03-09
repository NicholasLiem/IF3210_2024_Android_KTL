package com.ktl.bondoman

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktl.bondoman.ui.TestActivity
import com.ktl.bondoman.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            val testIntent = Intent(this, TestActivity::class.java)
            val editor = sharedPreferences.edit()
            editor.remove("token")
            editor.apply()
            startActivity(testIntent)
        }
        finish()
        return
    }
}
