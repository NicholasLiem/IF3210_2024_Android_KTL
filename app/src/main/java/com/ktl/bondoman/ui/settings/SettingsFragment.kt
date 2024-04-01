package com.ktl.bondoman.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.utils.ExporterUtils
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private lateinit var tokenManager: TokenManager

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
        val exportTransactionButton: Button = view.findViewById(R.id.export_transaction_button)
        val emailTransactionButton: Button = view.findViewById(R.id.email_transaction_button)
        val emailFormatRadioGroup: RadioGroup = view.findViewById(R.id.email_format_radio_group)
        val randomizeTransactionButton: Button = view.findViewById(R.id.randomize_transaction_button)
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

        exportTransactionButton.setOnClickListener {
            val fileType = getSelectedFileType(emailFormatRadioGroup)
            Log.w("SettingsFragment", "Exporting transactions")

            val transactionDao = (requireActivity().application as TransactionApplication).database.transactionDao()
            lifecycleScope.launch {
                ExporterUtils.exportTransactions(requireContext(), transactionDao, fileType)
            }
            val currentActivity = requireActivity()
            currentActivity.runOnUiThread {
                // Display toast
                Toast.makeText(
                    currentActivity,
                    "File exported successfully! See downloads folder",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        emailTransactionButton.setOnClickListener {
            val fileType = getSelectedFileType(emailFormatRadioGroup)

            val transactionDao = (requireActivity().application as TransactionApplication).database.transactionDao()
            lifecycleScope.launch {
                ExporterUtils.exportTransactions(requireContext(), transactionDao, fileType, true)
            }
            val currentActivity = requireActivity()
            currentActivity.runOnUiThread {
                // Display toast
                Toast.makeText(
                    currentActivity,
                    "Email generated",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        randomizeTransactionButton.setOnClickListener {
            val intent = Intent("com.ktl.bondoman.RANDOMIZE_TRANSACTION")
            requireActivity().applicationContext.sendBroadcast(intent)
        }
    }

    private fun getSelectedFileType(radioGroup: RadioGroup): String {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = view?.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton?.tag.toString()
    }
}