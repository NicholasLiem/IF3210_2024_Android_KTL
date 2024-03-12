package com.ktl.bondoman.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ktl.bondoman.db.Transaction
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionListFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((requireActivity().application as TransactionApplication).repository)
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Transactions"

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TransactionAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        transactionViewModel.allTransactions.observe(this, Observer { transactions ->
            transactions.let { adapter.submitList(it) }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Create a new instance of the fragment
            val newWordFragment = TransactionAddFragment.newInstance("", "", "", "", "")

            // Begin a fragment transaction
            getActivity()?.getSupportFragmentManager()?.beginTransaction()
                // Replace the content of the container with the new fragment
                ?.replace(R.id.nav_host_fragment, newWordFragment)
                // Add the transaction to the back stack (optional)
                ?.addToBackStack(null)
                // Commit the transaction
                ?.commit()
        }
        adapter.setOnDeleteClickListener { transaction ->
            // Handle delete button click
            transactionViewModel.delete(transaction)
        }
        adapter.setOnEditClickListener { transaction ->
            // Handle delete button click
            val newWordFragment = TransactionAddFragment.newInstance(transaction)

            // Redirect ke add fragment.
            // Gausah legit "edit" operation di DB, tapi IDnya harus sama.
            // Soalnya kalau conflict di set UPDATE,jadi kek upsert gitu.


            // Begin a fragment transaction
            getActivity()?.getSupportFragmentManager()?.beginTransaction()
                // Replace the content of the container with the new fragment
                ?.replace(R.id.nav_host_fragment, newWordFragment)
                // Add the transaction to the back stack (optional)
                ?.addToBackStack(null)
                // Commit the transaction
                ?.commit()
        }
    }
}