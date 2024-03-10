package com.ktl.bondoman

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ktl.bondoman.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token == null || isTokenExpired(token)) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        // Debugging purpose only
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()

        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }

    private fun isTokenExpired(token: String): Boolean {
    // TODO: Implement this
        return false
    }
}
