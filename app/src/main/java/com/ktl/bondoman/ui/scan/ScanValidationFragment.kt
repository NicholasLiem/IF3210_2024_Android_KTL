package com.ktl.bondoman.ui.twibbon

import NetworkReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R
import com.ktl.bondoman.network.ApiClient
import com.ktl.bondoman.network.requests.LoginRequest
import com.ktl.bondoman.token.TokenManager
import kotlinx.coroutines.launch

private const val ARG_IMG = "";

class ScanValidationFragment : Fragment() {
    private var img: String? = null
    private lateinit var tokenManager: TokenManager
    private lateinit var receiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            img = it.getString(ARG_IMG)
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkReceiver.getInstance()
        requireActivity().registerReceiver(receiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan_validation, container, false)
        val cancelButton: Button = view.findViewById<Button>(R.id.validationCancelButton)
        val sendButton: Button = view.findViewById<Button>(R.id.validationSendButton)
        val saveButton: Button = view.findViewById<Button>(R.id.validationSaveButton)

        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        sendButton.setOnClickListener {
            sendImage(img);
        }

        saveButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        val imageView: ImageView = view.findViewById(R.id.scanResultImage)
        val imageUri = Uri.parse(img)
        imageView.setImageURI(imageUri)

        saveButton.visibility = View.GONE

        return view;
    }

    companion object {
        fun newInstance(img : String) =
            ScanValidationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMG, img)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Twibbon Validation"
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }

    private fun sendImage(img: String) {
        if (!receiver.isConnected()) {
            Toast.makeText(requireContext(), "No internet connection, cannot send data!", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                var token = tokenManager.getTokenStr()



                val response = ApiClient.apiService.uploadBill(token,)

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
            }
        }

}