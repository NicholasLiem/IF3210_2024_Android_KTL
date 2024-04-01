package com.ktl.bondoman.ui.twibbon

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ktl.bondoman.R

private const val ARG_IMG = "";

class ScanValidationFragment : Fragment() {
    private var img: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            img = it.getString(ARG_IMG)
        }
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
}