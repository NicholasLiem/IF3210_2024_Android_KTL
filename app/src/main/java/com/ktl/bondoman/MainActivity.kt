package com.ktl.bondoman

import NetworkReceiver
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
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
import com.ktl.bondoman.ui.transaction.TransactionAddFragment
import com.ktl.bondoman.utils.PermissionUtils


class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var receiver: NetworkReceiver
    private lateinit var tokenExpiryReceiver: BroadcastReceiver
    private lateinit var randomizeTransactionReceiver: BroadcastReceiver

    private var isNetworkReceiverRegistered = false
    private var isTokenExpiryReceiverRegistered = false
    private var isRandomizeTransactionReceiverRegistered = false

    companion object {
        const val ACTION_TOKEN_EXPIRED = "com.ktl.bondoman.ACTION_TOKEN_EXPIRED"
        const val ACTION_RANDOMIZE_TRANSACTION = "com.ktl.bondoman.RANDOMIZE_TRANSACTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tokenManager = TokenManager(this)

        PermissionUtils.requestLocationPermissions(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE)

        setupTokenExpiryReceiver()
        setupRandomizeTransactionReceiver()
        setupNetworkReceiver()
        setupUIComponents()

        val token = tokenManager.loadToken()
        if (token == null || isTokenExpired(token)) {
            navigateToLogin()
        } else {
            if (JwtCheckService.isServiceRunning(this)) {
                JwtCheckService.stopJwtCheckService(this)
            }
            JwtCheckService.startJwtCheckService(this)
        }
    }

    override fun onDestroy() {
        if (isNetworkReceiverRegistered && receiver.isListening()) {
            applicationContext.unregisterReceiver(receiver)
            isNetworkReceiverRegistered = false
            receiver.setListening(false)
        }
        if (isTokenExpiryReceiverRegistered) {
            applicationContext.unregisterReceiver(tokenExpiryReceiver)
            isTokenExpiryReceiverRegistered = false
        }
        if (isRandomizeTransactionReceiverRegistered) {
            applicationContext.unregisterReceiver(randomizeTransactionReceiver)
            isRandomizeTransactionReceiverRegistered = false
        }
        super.onDestroy()
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

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            sideNavView.visibility = View.VISIBLE
            navView.visibility = View.GONE
            toolbar.visibility = View.GONE
        } else {
            sideNavView.visibility = View.GONE
            navView.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupTokenExpiryReceiver() {
        tokenExpiryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_TOKEN_EXPIRED) {
                    navigateToLogin()
                }
            }
        }
        val filter = IntentFilter(ACTION_TOKEN_EXPIRED)
        if (!isTokenExpiryReceiverRegistered){
            applicationContext.registerReceiver(tokenExpiryReceiver, filter)
            isTokenExpiryReceiverRegistered = true
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupRandomizeTransactionReceiver() {
        randomizeTransactionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_RANDOMIZE_TRANSACTION) {
                    navigateToAddTransaction()
                }
            }
        }

        val filter = IntentFilter(ACTION_RANDOMIZE_TRANSACTION)
        if (!isRandomizeTransactionReceiverRegistered) {
            applicationContext.registerReceiver(randomizeTransactionReceiver, filter)
            isRandomizeTransactionReceiverRegistered = true
        }
    }

    private fun setupNetworkReceiver() {
        // Registers BroadcastReceiver to track network connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkReceiver.getInstance()
        if (!isNetworkReceiverRegistered){
            applicationContext.registerReceiver(receiver, filter)
            isNetworkReceiverRegistered = true
            receiver.setListening(true);
        }
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

    private fun navigateToAddTransaction() {
        if (!isFinishing && !isDestroyed) {
            val randomTitle = "Transaction ${(1..100).random()}"
            val randomAmount = "%.2f".format((1..10000).random() / 100.0)
            val categories = listOf("Income", "Expense")
            val randomCategory = categories.random()
            val transactionAddFragment = TransactionAddFragment.newInstance(randomTitle, randomCategory, randomAmount)

            supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_host_fragment, transactionAddFragment)
                addToBackStack(null)
                commit()
            }
        }
    }


    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        val sideNavView = findViewById<NavigationView>(R.id.side_navigation_menu)
        val navView = findViewById<BottomNavigationView>(R.id.navigation_menu)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (newConfig.orientation === android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            sideNavView.visibility = View.VISIBLE
            navView.visibility = View.GONE
            toolbar.visibility = View.GONE
        } else if (newConfig.orientation === android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            sideNavView.visibility = View.GONE
            navView.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
        }
    }
}
