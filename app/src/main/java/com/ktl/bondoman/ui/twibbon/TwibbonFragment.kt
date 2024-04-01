package com.ktl.bondoman.ui.twibbon

import NetworkReceiver
import android.Manifest
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ktl.bondoman.R
import com.ktl.bondoman.databinding.FragmentTwibbonBinding


private const val TAG = "cameraX"
private const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
private const val REQUEST_CODE = 123
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class TwibbonFragment : Fragment() {
    private lateinit var viewBinding: FragmentTwibbonBinding
    private var imageCapture : ImageCapture? = null
    private lateinit var connectivityChangeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTwibbonBinding.inflate(inflater, container, false)
        val networkReceiver = NetworkReceiver.getInstance()

        if (!checkPermissions()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE)
        } else {
            startCamera()
        }

        return viewBinding.root
    }


    fun checkPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startCamera()
                Toast.makeText(requireContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Camera Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case
            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(viewBinding.cameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Set up the camera selector to use the default front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(TAG,"Start Camera Fails: ", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Twibbon"

        var twibbon1 : ImageView= view.findViewById(R.id.twibbon1);
        var twibbon2 : ImageView= view.findViewById(R.id.twibbon2);
        var twibbon3 : ImageView= view.findViewById(R.id.twibbon3);
        var twibbon4 : ImageView= view.findViewById(R.id.twibbon4);
        var twibbon5 : ImageView= view.findViewById(R.id.twibbon5);
        var overlay: ImageView = view.findViewById(R.id.twibbonOverlay);
        var captureButton : Button = view.findViewById<Button>(R.id.captureButton)

        // Set Default Val
        overlay.setImageResource(R.drawable.twibbon1)
        var currentTwibbon = "twibbon1";

        // When button is clicked
        twibbon1.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon1)
            currentTwibbon = "twibbon1";
        }
        twibbon2.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon2)
            currentTwibbon = "twibbon2";
        }
        twibbon3.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon3)
            currentTwibbon = "twibbon3";
        }
        twibbon4.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon4)
            currentTwibbon = "twibbon4";
        }
        twibbon5.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon5)
            currentTwibbon = "twibbon5";
        }

        captureButton.setOnClickListener {
            val validationFrag = TwibbonValidationFragment.newInstance();
            getActivity()?.supportFragmentManager?.beginTransaction()
                ?.replace(com.ktl.bondoman.R.id.nav_host_fragment, validationFrag)
                ?.addToBackStack(null)
                ?.commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}