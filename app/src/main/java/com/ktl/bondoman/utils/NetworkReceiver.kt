package com.ktl.bondoman.utils

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo


class NetworkReceiver : BroadcastReceiver() {

    private var connected: Boolean = false
    private var listening : Boolean = false
    private var show : Boolean = false

    companion object {
        private var instance: NetworkReceiver? = null
        const val ACTION_CONNECTIVITY_CHANGE = "com.ktl.bondoman.CONNECTIVITY_CHANGE"
        const val EXTRA_CONNECTED = "connected"

        @Synchronized
        fun getInstance(): NetworkReceiver {
            if (instance == null) {
                instance = NetworkReceiver()
            }
            return instance!!
        }

    }

    fun setListening(bool: Boolean) {
        this.listening = bool
    }

    fun isListening(): Boolean {
        return this.listening
    }
    fun isConnected(): Boolean {
        return this.connected
    }
    fun setShow(bool: Boolean) {
        this.show = bool
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val dialogBuilder = AlertDialog.Builder(context)
        this.connected = isOnline(context!!)
        val message = if (this.connected) "You are now online!" else "Your device is now offline, some features may not work!"

        if (show) {
            dialogBuilder.setMessage(message)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }

        // Broadcast connectivity change
        val connectivityIntent = Intent(ACTION_CONNECTIVITY_CHANGE).apply {
            putExtra(EXTRA_CONNECTED, connected)
        }
        context.sendBroadcast(connectivityIntent)
    }

    private fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo

        return networkInfo?.isConnected == true
    }
}