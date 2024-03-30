package com.ktl.bondoman.ui.twibbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ktl.bondoman.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TWIBBON = "";
private const val ARG_IMG = "";

/**
 * A simple [Fragment] subclass.
 * Use the [GraphFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TwibbonValidationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var twibbon: String? = null
    private var img: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            twibbon = it.getString(ARG_TWIBBON)
            img = it.getString(ARG_IMG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_twibbon_validation, container, false)
        val cancelButton: Button = view.findViewById<Button>(R.id.retakeButton);
        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
        return view;
    }

    companion object {

        fun newInstance() =
            TwibbonValidationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TWIBBON, twibbon)
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