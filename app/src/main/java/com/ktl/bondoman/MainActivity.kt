package com.ktl.bondoman

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ktl.bondoman.token.Token
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.auth.LoginActivity
import com.ktl.bondoman.utils.PermissionUtils
import com.ktl.bondoman.workers.TokenExpiryWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var prefsListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tokenManager = TokenManager(this)

        PermissionUtils.requestLocationPermissions(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE)
        setupPrefsListener()
        scheduleTokenExpiryCheck()

        val token = tokenManager.loadToken()
        if (token == null || isTokenExpired(token)) {
            navigateToLogin()
        }

        setupUIComponents()
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    private fun scheduleTokenExpiryCheck() {
        val workRequest = OneTimeWorkRequestBuilder<TokenExpiryWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniqueWork("TokenExpiryCheck", ExistingWorkPolicy.REPLACE, workRequest)
    }

    private fun setupUIComponents() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }
    private fun setupPrefsListener() {
        val sharedPreferences = tokenManager.getSharedPreferences()
        prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "tokenExpired" && prefs.getBoolean(key, false)) {
                with(prefs.edit()) {
                    remove("tokenExpired")
                    apply()
                }
                navigateToLogin()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    private fun isTokenExpired(token: Token): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val expMillis = token.exp!! * 1000
        return currentTimeMillis > expMillis
    }

    private fun navigateToLogin() {
        TokenManager(this).clearToken()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
