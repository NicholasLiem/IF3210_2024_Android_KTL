package com.ktl.bondoman.ui.transaction

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import android.widget.Toast
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent

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
        arguments?.let {
            // Parse arguments
            nim = it.getString(ARG_PARAM1)
            title = it.getString(ARG_PARAM2)
            category = it.getString(ARG_PARAM3)
            amount = it.getString(ARG_PARAM4)
            location = it.getString(ARG_PARAM5)
            id = it.getLong(ARG_ID)
        }
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


        nimEditText.setText(nim)
        titleEditText.setText(title)
        amountEditText.setText(amount)
        locationEditText.setText(location)

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
            val checkedRadioButtonId = categoryRadioGroup.checkedRadioButtonId
            val category = when (checkedRadioButtonId) {
                R.id.radioButtonIncome -> "Pemasukan"
                R.id.radioButtonExpense -> "Pengeluaran"
                else -> "" // Handle default case
            }


            // Insert data into database
            transactionViewModel.insert(
                Transaction(id, nim, title, category, amount, location))

            val currentActivity = requireActivity()

            currentActivity.runOnUiThread(Runnable {
                // Display toast
                Toast.makeText(currentActivity, "Transaction added successfully", Toast.LENGTH_SHORT).show()
            })

            // Begin a fragment transaction
            getActivity()?.getSupportFragmentManager()?.popBackStackImmediate();
        }
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionAddFragment.
         */
        // TODO: Rename and change types and number of parameters
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
}