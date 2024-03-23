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
import com.ktl.bondoman.token.TokenManager

class TransactionListFragment : Fragment() {
    private lateinit var tokenManager: TokenManager
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((requireActivity().application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
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
            val newWordFragment = tokenManager.loadToken()?.nim?.let { nim ->
                TransactionAddFragment.newInstance(
                    nim, "", "", "", "")
            }

            // Begin a fragment transaction
            if (newWordFragment != null) {
                getActivity()?.supportFragmentManager?.beginTransaction()
                    // Replace the content of the container with the new fragment
                    ?.replace(R.id.nav_host_fragment, newWordFragment)
                    // Add the transaction to the back stack (optional)
                    ?.addToBackStack(null)
                    // Commit the transaction
                    ?.commit()
            }
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