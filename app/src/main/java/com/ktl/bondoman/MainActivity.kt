package com.ktl.bondoman

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.ui.auth.LoginActivity
import com.ktl.bondoman.viewmodel.TransactionViewModel
import com.ktl.bondoman.viewmodel.TransactionViewModelFactory


class MainActivity : AppCompatActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
//        transactionViewModel.insert(Transaction(1, "test33", "test", "test", 123.2,""))
//        transactionViewModel.allTransactions.observe(this) { transactions ->
//            // print
//            transactions.forEach {
//                Log.w("Transaction", it.toString())
//            }
//        }
        if (token == null || isTokenExpired(token)) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }


    private fun isTokenExpired(token: String): Boolean {
    // TODO: Implement this
        return false
    }
}
