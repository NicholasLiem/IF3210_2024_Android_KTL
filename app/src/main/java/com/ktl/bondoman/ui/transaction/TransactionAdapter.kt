package com.ktl.bondoman.ui.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ktl.bondoman.R
import com.ktl.bondoman.db.Transaction

class TransactionAdapter (private val nim : String) : ListAdapter<Transaction, TransactionViewHolder>(TransactionsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent, nim)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, editClickListener, deleteClickListener, itemClickListener)
    }

    // Click listeners for edit and delete buttons
    private var editClickListener: ((Transaction) -> Unit)? = null
    private var deleteClickListener: ((Transaction) -> Unit)? = null
    private var itemClickListener: ((Transaction) -> Unit)? = null

    // Function to set edit button click listener
    fun setOnEditClickListener(listener: (Transaction) -> Unit) {
        editClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Transaction) -> Unit) {
        deleteClickListener = listener
    }

    fun setItemClickListener(listener: (Transaction) -> Unit) {
        itemClickListener = listener
    }
}

class TransactionViewHolder(itemView: View, private val nim: String) : RecyclerView.ViewHolder(itemView) {
    private val titleView: TextView = itemView.findViewById(R.id.titleView)
    private val dateView: TextView = itemView.findViewById(R.id.dateView)
    private val amountView: TextView = itemView.findViewById(R.id.amountView)
    private val locationView: TextView = itemView.findViewById(R.id.locationView)
    private val categoryView: TextView = itemView.findViewById(R.id.categoryView)
    private val nimView: TextView = itemView.findViewById(R.id.nimView)

    private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
    private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)


    @SuppressLint("SetTextI18n")
    fun bind(current: Transaction, editClickListener: ((Transaction) -> Unit)?, deleteClickListener: ((Transaction) -> Unit)?, itemClickListener: ((Transaction) -> Unit)?) {
        titleView.text = current.title
        dateView.text = Transaction.getDateString(current.date)
        amountView.text = "Amount: \n$ " + "%,.3f".format(current.amount)
        locationView.text = "Location: \n" + current.location
        categoryView.text =  current.category
//        Accessibility reasons
//        if (current.category == "Income"){
//            categoryView.setTextColor(Color.parseColor("#B54B88F1"))
//        } else {
//            categoryView.setTextColor(Color.parseColor("#DCED4E4E"))
//        }
        nimView.text = "by " + current.nim

        if (nim != current.nim){
            buttonEdit.visibility = View.GONE
            buttonDelete.visibility = View.GONE
        } else {
            buttonEdit.visibility = View.VISIBLE
            buttonDelete.visibility = View.VISIBLE
        }

        buttonEdit.setOnClickListener {
            editClickListener?.invoke(current)
        }

        buttonDelete.setOnClickListener {
            deleteClickListener?.invoke(current)
        }

        itemView.setOnClickListener {
            itemClickListener?.invoke(current)
        }
    }

    companion object {
        fun create(parent: ViewGroup, nim : String): TransactionViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            return TransactionViewHolder(view, nim)
        }
    }
}
class TransactionsComparator : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem:Transaction): Boolean {
        // make sure all transaction attributes are the same
        // not just id
        return (oldItem.id == newItem.id
                && oldItem.date == newItem.date
//                && oldItem.title.equals(newItem.title)
                && oldItem.amount == newItem.amount)
    }
}
