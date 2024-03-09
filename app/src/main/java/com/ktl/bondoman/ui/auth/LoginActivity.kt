package com.ktl.bondoman.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R
import com.ktl.bondoman.network.ApiClient
import com.ktl.bondoman.network.requests.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.login_email_field)
        val passwordEditText = findViewById<EditText>(R.id.login_password_field)
        val signInButton = findViewById<Button>(R.id.sign_in_button)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.token
                    getSharedPreferences("appPreferences", Context.MODE_PRIVATE).edit().apply {
                        putString("token", token)
                        apply()
                    }

                    val mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}