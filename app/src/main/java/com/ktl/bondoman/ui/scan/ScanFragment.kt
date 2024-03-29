package com.ktl.bondoman.ui.scan

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ktl.bondoman.MainActivity
import com.ktl.bondoman.databinding.FragmentScanBinding

private const val TAG = "cameraX"
private const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
private const val REQUEST_CODE = 123
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class ScanFragment : Fragment() {
    private lateinit var viewBinding: FragmentScanBinding
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
        viewBinding = FragmentScanBinding.inflate(inflater, container, false)
        connectivityChangeReceiver = ConnectivityChangeReceiver(this)
        val networkReceiver = NetworkReceiver.getInstance()

        if (!networkReceiver.isConnected()) {
            viewBinding.noInternetLayout.visibility = View.VISIBLE
            viewBinding.scanLayout.visibility = View.GONE
        } else {
            viewBinding.noInternetLayout.visibility = View.GONE
            viewBinding.scanLayout.visibility = View.VISIBLE
            if (!checkPermissions()) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE)
            } else {
                startCamera()
            }
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

            // Set up the camera selector to use the default back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

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
        activity.supportActionBar?.title = "Scan"
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            connectivityChangeReceiver,
            IntentFilter(NetworkReceiver.ACTION_CONNECTIVITY_CHANGE)
        )
    }

    override fun onStop() {
        super.onStop()
        // Unregister broadcast receiver
        requireContext().unregisterReceiver(connectivityChangeReceiver)
    }

    class ConnectivityChangeReceiver(private val fragment: ScanFragment) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isConnected = intent?.getBooleanExtra(NetworkReceiver.EXTRA_CONNECTED, false) ?: false

            // Access the view binding object directly to update UI
            if (fragment.viewBinding.noInternetLayout.visibility == View.VISIBLE && isConnected) {
                fragment.viewBinding.noInternetLayout.visibility = View.GONE
                fragment.viewBinding.scanLayout.visibility = View.VISIBLE
                if (!fragment.checkPermissions()) {
                    fragment.requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE)
                } else {
                    fragment.startCamera()
                }
            } else if (fragment.viewBinding.noInternetLayout.visibility == View.GONE && !isConnected) {
                fragment.viewBinding.noInternetLayout.visibility = View.VISIBLE
                fragment.viewBinding.scanLayout.visibility = View.GONE
            }
        }
    }
}