package com.ktl.bondoman.ui.transaction

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.components.LoadingButton
import com.ktl.bondoman.utils.LocationUtils
import com.ktl.bondoman.utils.PermissionUtils

private const val ARG_ID = "id"
private const val ARG_NIM = "nim"
private const val ARG_TITLE = "title"
private const val ARG_CATEGORY = "category"
private const val ARG_AMOUNT = "amount"
private const val ARG_LOCATION = "location"

class TransactionAddFragment : Fragment() {

    private lateinit var tokenManager: TokenManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((requireActivity().application as TransactionApplication).repository)
    }

    // Parameters received from arguments
    private var nim: String? = null
    private var title: String? = null
    private var category: String? = null
    private var amount: String? = null
    private var location: String? = null
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        nim = tokenManager.loadToken()?.nim
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        parseArguments()
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_add, container, false)

        // Initialize views
        val nimEditText = view.findViewById<TextView>(R.id.editTextNim)
        val titleEditText = view.findViewById<TextView>(R.id.editTextTitle)
        val amountEditText = view.findViewById<TextView>(R.id.editTextAmount)
        val locationEditText = view.findViewById<TextView>(R.id.editTextLocation)
        val loadingButtonTransactionSubmit = view.findViewById<LoadingButton>(R.id.loadingButtonSubmit)
        loadingButtonTransactionSubmit.setButtonText("Submit")
        val submitButton = loadingButtonTransactionSubmit.getButton()
        val cancelButton = view.findViewById<Button>(R.id.buttonCancel)
        val categoryRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupCategory)

        nimEditText.text = nim
        titleEditText.text = title
        amountEditText.text = amount

        // Autofill location
        if (!location.isNullOrEmpty()) {
            //if (PermissionUtils.hasLocationPermission) {}
            locationEditText.text = location
        } else {
            LocationUtils.getLastKnownLocation(requireActivity()) { locationString ->
                locationEditText.text = locationString
            }
        }

        // set category
        fun setCategory(category: String?) {
            when (category) {
                "Income" -> categoryRadioGroup.check(R.id.radioButtonIncome)
                "Expense" -> categoryRadioGroup.check(R.id.radioButtonExpense)
                else -> categoryRadioGroup.clearCheck() // Clear selection if category is not recognized
            }
        }

        // Call setCategory function with your desired category value
        setCategory(category)

        submitButton.setOnClickListener {
            loadingButtonTransactionSubmit.showLoadingWithDelay(true, 500)
            // Get input values
            val nim = nimEditText.text.toString()
            val title = titleEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            var location = locationEditText.text.toString()
            val category = when (categoryRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonIncome -> "Income"
                R.id.radioButtonExpense -> "Expense"
                else -> "" // Handle default case
            }
            if (validateForm(title, amount, category, location)) {
                // Sets default value for location if not provided
                if (location.isEmpty()) {
                    location = "Lat: -6.8733093, Lon: 107.6050709"
                }
                // Insert data into database
                transactionViewModel.insert(
                    Transaction(id, nim, title, category, amount, location))
                    val currentActivity = requireActivity()
                 currentActivity.runOnUiThread {
                     // Display toast
                     Toast.makeText(
                         currentActivity,
                         "Transaction added successfully",
                         Toast.LENGTH_SHORT
                     ).show()
                 }
                    // Begin a fragment transaction
                activity?.supportFragmentManager?.popBackStackImmediate()
            } else {
                loadingButtonTransactionSubmit.showLoadingWithDelay(false, 500)
            }
        }

        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(nim: String, title: String, category: String, amount: String, location: String) =
            TransactionAddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NIM, nim)
                    putString(ARG_TITLE, title)
                    putString(ARG_CATEGORY, category)
                    putString(ARG_AMOUNT, amount)
                    putString(ARG_LOCATION, location)
                }
            }
        fun newInstance(transaction: Transaction) =
            TransactionAddFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, transaction.id)
                    putString(ARG_NIM, transaction.nim)
                    putString(ARG_TITLE, transaction.title)
                    putString(ARG_CATEGORY, transaction.category)
                    putString(ARG_AMOUNT, transaction.amount.toString())
                    putString(ARG_LOCATION, transaction.location)
                }
            }

        fun newInstance(title: String, category: String, amount: String) =
            TransactionAddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_CATEGORY, category)
                    putString(ARG_AMOUNT, amount)
                }
            }
    }

    private fun parseArguments() {
        arguments?.let {
            title = it.getString(ARG_TITLE)
            category = it.getString(ARG_CATEGORY)
            amount = it.getString(ARG_AMOUNT)
            location = it.getString(ARG_LOCATION)
            id = it.getLong(ARG_ID, 0)
        }
    }

    private fun validateForm(title: String, amount: Double, category: String, location: String): Boolean {
        if (title.isEmpty() || !title.matches(Regex("^[a-zA-Z\\s]+$"))) {
            Toast.makeText(requireContext(), "Invalid title. Please use only letters and spaces.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (amount <= 0.0) {
            Toast.makeText(requireContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
            return false
        }

        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }

        if (location.isNotEmpty() && !location.matches(Regex("^[^:.*]*[:.]?[^:.*]*$"))) {
            Toast.makeText(requireContext(), "Invalid location. It can only include ':' or '.'", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationUtils.getLastKnownLocation(requireActivity()) { locationString ->
                    val locationEditText = view?.findViewById<TextView>(R.id.editTextLocation)
                    locationEditText?.text = locationString
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
