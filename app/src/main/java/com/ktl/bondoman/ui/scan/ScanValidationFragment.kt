package com.ktl.bondoman.ui.twibbon

import NetworkReceiver
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.network.ApiClient
import com.ktl.bondoman.network.requests.BillUploadRequest
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.transaction.TransactionViewModel
import com.ktl.bondoman.ui.transaction.TransactionViewModelFactory
import kotlinx.coroutines.launch

private const val ARG_IMG = "";

data class Item (
    val name : String,
    val price : Double
)

class ScanValidationFragment : Fragment() {
    private var img: String? = null
    private lateinit var tokenManager: TokenManager
    private lateinit var receiver: NetworkReceiver
    private var itemArr = mutableListOf<Item>()
    private var itemArrStr : String = ""
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((requireActivity().application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.let {
            img = it.getString(ARG_IMG)
        }

        tokenManager = TokenManager(requireContext())

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
        val contentView: TextView = view!!.findViewById(R.id.scanContent)
        val scrollContentView: ScrollView = view!!.findViewById(R.id.scrollScanContent)

        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        sendButton.setOnClickListener {
            sendImage(img!!);
        }

        saveButton.setOnClickListener {
            saveContent();
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        val imageView: ImageView = view.findViewById(R.id.scanResultImage)
        val imageUri = Uri.parse(img)
        imageView.setImageURI(imageUri)

        contentView.visibility = View.GONE
        saveButton.visibility = View.GONE
        scrollContentView.visibility = View.GONE

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
        activity.supportActionBar?.title = "Scan Validation"
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
                val token = tokenManager.getTokenStr()

                val dir = Environment.getExternalStorageDirectory()
                val path = dir.absolutePath
                var imgPath = img.substringAfter("/bondoman")
                imgPath = path + imgPath


                val response = ApiClient.apiService.uploadBill("Bearer $token", BillUploadRequest(imgPath).toMultipartBodyPart())

                Toast.makeText(requireContext(), "Request is sent.", Toast.LENGTH_SHORT).show()

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    val itemsContainer = responseBody?.items
                    val itemsList = itemsContainer?.arrItems

                    var lineData = "Index. Name : Final Price \n\n"
                    itemArrStr += lineData

                    var count = 1;
                    itemsList?.forEach{
                        var name = it.name
                        var qty = it.qty
                        var price = it.price
                        var final_price = qty * price;

                        Log.i("RESPONSE", "$name : $final_price")
                        itemArr.add(Item(name, final_price))

                        lineData = "$count. $name : $final_price \n"
                        itemArrStr += lineData
                        lineData = "\t\t $qty @$price \n"
                        itemArrStr += lineData

                        count++
                    }
//                    Toast.makeText(requireContext(), "Response is successfully taken.", Toast.LENGTH_SHORT)
                    changeState()

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Successfully get a response", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.d("ERROR", "${response.raw()}")
                    Toast.makeText(requireContext(), "Not Successful. Response Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                    Log.d("ERROR", "${e.localizedMessage}")
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun changeState(){
        val title: TextView = view!!.findViewById<TextView>(R.id.scanResultTitle)
        val imageView: ImageView = view!!.findViewById(R.id.scanResultImage)
        val contentView: TextView = view!!.findViewById(R.id.scanContent)
        val scrollContentView : ScrollView = view!!.findViewById(R.id.scrollScanContent)
        val sendButton : Button = view!!.findViewById(R.id.validationSendButton)
        val saveButton : Button = view!!.findViewById(R.id.validationSaveButton)
        imageView.visibility = View.GONE
        sendButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        title.text = "Processed Transaction"
        contentView.visibility = View.VISIBLE
        scrollContentView.visibility = View.VISIBLE
        contentView.text = itemArrStr
    }

    private fun saveContent(){
        tokenManager = TokenManager(requireContext())
        // Default value
        val id : Long = 0
        val nim = tokenManager.loadToken()?.nim
        val location = "Lat: -6.8733093, Lon: 107.6050709"
        val rnds = (0..10).random()
        var category = "Expense"
        if (rnds < 5){
            category = "Income"
        }

        itemArr.forEach {
            val name = it.name
            val price = it.price

            // Insert Data
            transactionViewModel.insert(
                Transaction(id, nim!!, name, category, price, location)
            )
        }

        val currentActivity = requireActivity()
        currentActivity.runOnUiThread {
            // Display toast
            Toast.makeText(
                currentActivity,
                "Transactions have been added successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}