package com.ktl.bondoman.ui.auth

import NetworkReceiver
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R
import com.ktl.bondoman.network.ApiClient
import com.ktl.bondoman.network.requests.LoginRequest
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.components.LoadingButton
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var receiver: NetworkReceiver
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tokenManager = TokenManager(this)

        val emailEditText = findViewById<EditText>(R.id.login_email_field)
        val passwordEditText = findViewById<EditText>(R.id.login_password_field)
        loadingButton = findViewById(R.id.loading_button)
        loadingButton.setButtonText("Sign In")
        val signInButton = loadingButton.getButton()

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loadingButton.showLoading(true)
            login(email, password)
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkReceiver.getInstance()
        this.registerReceiver(receiver, filter)
    }

    @SuppressLint("SetTextI18n")
    private fun login(email: String, password: String) {
        if (!receiver.isConnected()) {
            Toast.makeText(this, "No internet connection, cannot login!", Toast.LENGTH_SHORT).show()
            loadingButton.showLoading(false)
            return
        }

        lifecycleScope.launch {
            try {
                loadingButton.showLoadingWithDelay(true, 1000)
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.token
                    token?.let {
                        tokenManager.saveTokenRaw(it)
                    }

                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainIntent)

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
            } finally {
                loadingButton.showLoading(false)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(receiver)
    }
}