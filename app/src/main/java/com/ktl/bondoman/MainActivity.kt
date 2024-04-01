package com.ktl.bondoman

import NetworkReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.ktl.bondoman.services.JwtCheckService
import com.ktl.bondoman.token.Token
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.auth.LoginActivity
import com.ktl.bondoman.utils.PermissionUtils


class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var receiver: NetworkReceiver
    private lateinit var tokenExpiryReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tokenManager = TokenManager(this)

        PermissionUtils.requestLocationPermissions(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE)
        setupTokenExpiryReceiver()

        val token = tokenManager.loadToken()
        if (token == null || isTokenExpired(token)) {
            navigateToLogin()
        } else {
            startJwtCheckService()
        }

        setupUIComponents()

        // Registers BroadcastReceiver to track network connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkReceiver.getInstance()
        this.registerReceiver(receiver, filter)

        val sideNavView = findViewById<NavigationView>(R.id.side_navigation_menu)
        val navView = findViewById<BottomNavigationView>(R.id.navigation_menu)
        sideNavView.visibility = View.GONE
        navView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(receiver)
    }

    private fun setupUIComponents() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

            val sideNavView = findViewById<NavigationView>(R.id.side_navigation_menu)
            val navView = findViewById<BottomNavigationView>(R.id.navigation_menu)
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            navView.setupWithNavController(navController)
            sideNavView.setupWithNavController(navController)
    }

<<<<<<< HEAD
    private fun setupPrefsListener() {
        val sharedPreferences = tokenManager.getSharedPreferences()
        prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "tokenExpired" && prefs.getBoolean(key, false)) {
                with(prefs.edit()) {
                    remove("tokenExpired")
                    apply()
=======
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupTokenExpiryReceiver() {
        tokenExpiryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.ktl.bondoman.ACTION_TOKEN_EXPIRED") {
                    navigateToLogin()
>>>>>>> 29462a7dee5cdb2e3c7412be443e860da98144d2
                }
            }
        }
        val filter = IntentFilter("com.ktl.bondoman.ACTION_TOKEN_EXPIRED")
        applicationContext.registerReceiver(tokenExpiryReceiver, filter)
    }

    private fun stopJwtCheckService() {
        val serviceIntent = Intent(this, JwtCheckService::class.java)
        stopService(serviceIntent)
    }

    private fun isTokenExpired(token: Token): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val expMillis = token.exp!! * 1000
        return currentTimeMillis > expMillis
    }

    private fun navigateToLogin() {
        TokenManager(this).clearToken()
        val intent = Intent(this, LoginActivity::class.java)
        stopJwtCheckService()
        startActivity(intent)
        finish()
    }
<<<<<<< HEAD
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        val sideNavView = findViewById<NavigationView>(R.id.side_navigation_menu)
        val navView = findViewById<BottomNavigationView>(R.id.navigation_menu)
        if (newConfig.orientation === android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            sideNavView.visibility = View.VISIBLE
            navView.visibility = View.GONE
        } else if (newConfig.orientation === android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            sideNavView.visibility = View.GONE
            navView.visibility = View.VISIBLE
        }
    }

=======

    private fun startJwtCheckService() {
        val serviceIntent = Intent(this, JwtCheckService::class.java)
        startService(serviceIntent)
    }
>>>>>>> 29462a7dee5cdb2e3c7412be443e860da98144d2
}
