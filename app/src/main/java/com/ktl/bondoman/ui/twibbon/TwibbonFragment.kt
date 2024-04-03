package com.ktl.bondoman.ui.twibbon

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
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
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ktl.bondoman.R
import com.ktl.bondoman.databinding.FragmentTwibbonBinding
import com.ktl.bondoman.ui.components.LoadingButton
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale


private const val TAG = "cameraX"
private const val REQUEST_CODE = 123
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class TwibbonFragment : Fragment() {
    private lateinit var viewBinding: FragmentTwibbonBinding
    private var imageCapture : ImageCapture? = null
    private var currentTwibbon : String = "twibbon1"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTwibbonBinding.inflate(inflater, container, false)
        if (!checkPermissions()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE)
        } else {
            startCamera()
        }

        return viewBinding.root
    }


    private fun checkPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    @Deprecated("Deprecated in Java")
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

    private fun startCamera(){
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

        val captureLoadingButton: LoadingButton = view.findViewById(R.id.loading_button)
        captureLoadingButton.setButtonText("Capture")
        val twibbon1 : ImageView= view.findViewById(R.id.twibbon1)
        val twibbon2 : ImageView= view.findViewById(R.id.twibbon2)
        val twibbon3 : ImageView= view.findViewById(R.id.twibbon3)
        val twibbon4 : ImageView= view.findViewById(R.id.twibbon4)
        val twibbon5 : ImageView= view.findViewById(R.id.twibbon5)
        val overlay: ImageView = view.findViewById(R.id.twibbonOverlay)
        val captureButton : Button = captureLoadingButton.getButton()

        // Set Default Val
        overlay.setImageResource(R.drawable.twibbon1)

        // When button is clicked
        twibbon1.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon1)
            currentTwibbon = "twibbon1"
        }
        twibbon2.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon2)
            currentTwibbon = "twibbon2"
        }
        twibbon3.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon3)
            currentTwibbon = "twibbon3"
        }
        twibbon4.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon4)
            currentTwibbon = "twibbon4"
        }
        twibbon5.setOnClickListener {
            overlay.setImageResource(R.drawable.twibbon5)
            currentTwibbon = "twibbon5"
        }

        captureButton.setOnClickListener {
            captureLoadingButton.showLoadingWithDelay(true, 500)
            takePhoto()
        }

    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun saveBitmapToFile(bitmap: Bitmap?, file: File) {
        FileOutputStream(file).use { out ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    // Change the image proxy to bit map
                    val bitmap = imageProxyToBitmap(image)
                    image.close()

                    val tempFile = createTempFile()
                    saveBitmapToFile(bitmap, tempFile)

                    val savedUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.ktl.bondoman.fileprovider",
                        tempFile
                    )

                    val validationFrag = TwibbonValidationFragment.newInstance(currentTwibbon, savedUri.toString())
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, validationFrag)
                        ?.addToBackStack(null)
                        ?.commit()
                }

            }
        )
    }

    private fun createTempFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            deleteOnExit()
        }
    }

}