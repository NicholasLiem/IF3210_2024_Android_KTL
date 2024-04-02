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

private const val ARG_TWIBBON = "twibbon"
private const val ARG_IMG = "img"

class TwibbonValidationFragment : Fragment() {
    private var twibbon: String? = null
    private var img: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_twibbon_validation, container, false)
        val cancelButton: Button = view.findViewById(R.id.retakeButton)
        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        val overlay: ImageView = view.findViewById(R.id.twibbonOverlayValidation)

        if (twibbon === "twibbon1"){
            overlay.setImageResource(R.drawable.twibbon1)
        } else if (twibbon === "twibbon2"){
            overlay.setImageResource(R.drawable.twibbon2)
        } else if (twibbon === "twibbon3"){
            overlay.setImageResource(R.drawable.twibbon3)
        } else if (twibbon === "twibbon4"){
            overlay.setImageResource(R.drawable.twibbon4)
        } else if (twibbon === "twibbon5"){
            overlay.setImageResource(R.drawable.twibbon5)
        }

        val imageView: ImageView = view.findViewById(R.id.cameraViewValidation)
        val imageUri = Uri.parse(img)
        imageView.setImageURI(imageUri)

        return view
    }

    companion object {
        fun newInstance(twibbon: String, img: String) =
            TwibbonValidationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TWIBBON, twibbon)
                    putString(ARG_IMG, img)
                }
            }
    }

    private fun parseArguments() {
        arguments?.let {
            twibbon = it.getString(ARG_TWIBBON)
            img = it.getString(ARG_IMG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Twibbon Validation"
    }
}