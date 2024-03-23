package com.ktl.bondoman.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R
import com.ktl.bondoman.token.TokenManager

class SettingsFragment : Fragment() {
    private lateinit var tokenManager: TokenManager
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Settings"

        val logOutButton: Button = view.findViewById(R.id.log_out_button)

        logOutButton.setOnClickListener {
            val sharedPreferences = tokenManager.getSharedPreferences()
            sharedPreferences.edit()?.apply {
                tokenManager.clearToken()
                apply()
            }

            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}