package com.ktl.bondoman.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import android.widget.Toast
import com.ktl.bondoman.token.TokenManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "nim"
private const val ARG_PARAM2 = "title"
private const val ARG_PARAM3 = "category"
private const val ARG_PARAM4 = "amount"
private const val ARG_PARAM5 = "location"
private const val ARG_ID = "id"


/**
 * A simple [Fragment] subclass.
 * Use the [TransactionAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionAddFragment : Fragment() {

    private lateinit var tokenManager: TokenManager
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
        // Maybe add validation if nim is not there
        parseArguments()
    }


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
        val submitButton = view.findViewById<Button>(R.id.buttonSubmit)
        val categoryRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupCategory)


        nimEditText.text = nim
        titleEditText.text = title
        amountEditText.text = amount
        locationEditText.text = location

        // set category
        fun setCategory(category: String?) {
            when (category) {
                "Pemasukan" -> categoryRadioGroup.check(R.id.radioButtonIncome)
                "Pengeluaran" -> categoryRadioGroup.check(R.id.radioButtonExpense)
                else -> categoryRadioGroup.clearCheck() // Clear selection if category is not recognized
            }
        }

        // Call setCategory function with your desired category value
        setCategory(category)


        submitButton.setOnClickListener {
            // Get input values
            val nim = nimEditText.text.toString()
            val title = titleEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val location = locationEditText.text.toString()
            val category = when (categoryRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonIncome -> "Pemasukan"
                R.id.radioButtonExpense -> "Pengeluaran"
                else -> "" // Handle default case
            }

            if (validateForm(title, amount, location, category)) {
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
            }
        }
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(nim: String, title: String, category: String, amount: String, location: String) =
            TransactionAddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, nim)
                    putString(ARG_PARAM2, title)
                    putString(ARG_PARAM3, category)
                    putString(ARG_PARAM4, amount)
                    putString(ARG_PARAM5, location)
                }
            }
        fun newInstance(transaction: Transaction) =
            TransactionAddFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, transaction.id)
                    putString(ARG_PARAM1, transaction.nim)
                    putString(ARG_PARAM2, transaction.title)
                    putString(ARG_PARAM3, transaction.category)
                    putString(ARG_PARAM4, transaction.amount.toString())
                    putString(ARG_PARAM5, transaction.location)
                }
            }
    }

    private fun parseArguments() {
        arguments?.let {
            title = it.getString(ARG_PARAM2)
            category = it.getString(ARG_PARAM3)
            amount = it.getString(ARG_PARAM4)
            location = it.getString(ARG_PARAM5)
            id = it.getLong(ARG_ID, 0)
        }
    }

    private fun validateForm(title: String, amount: Double, location: String, category: String): Boolean {
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (amount <= 0.0) {
            Toast.makeText(requireContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
            return false
        }

        if (location.isEmpty()) {
            Toast.makeText(requireContext(), "Location cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}