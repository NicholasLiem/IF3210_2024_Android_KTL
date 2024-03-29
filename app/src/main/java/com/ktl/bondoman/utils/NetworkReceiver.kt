import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


class NetworkReceiver() : BroadcastReceiver() {

    private var connected: Boolean = false
    private var initial: Boolean = true

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

    fun isConnected(): Boolean {
        return this.connected
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val dialogBuilder = AlertDialog.Builder(context)
        this.connected = isOnline(context!!)
        val message = if (this.connected) "You are now online!" else "Your device is now offline, some features may not work!"

        dialogBuilder.setMessage(message)

        if (!this.initial) {
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }

        // Broadcast connectivity change
        val connectivityIntent = Intent(ACTION_CONNECTIVITY_CHANGE).apply {
            putExtra(EXTRA_CONNECTED, connected)
        }
        context.sendBroadcast(connectivityIntent)
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo // this returns a

        return networkInfo?.isConnected == true
    }
}