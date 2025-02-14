package com.ktl.bondoman.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ktl.bondoman.databinding.FragmentScanBinding
import com.ktl.bondoman.utils.NetworkReceiver
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

private const val TAG = "cameraX"
private const val REQUEST_CODE = 123
private const val IMAGE_REQUEST_CODE = 100
private val REQUIRED_PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
private const val targetSize : Long = 100 * 1024

private val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)

class ScanFragment : Fragment() {
    private lateinit var viewBinding: FragmentScanBinding
    private var imageCapture : ImageCapture? = null
    private lateinit var connectivityChangeReceiver: BroadcastReceiver

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
                requestPermissions(REQUIRED_PERMISSIONS_STORAGE, REQUEST_CODE)
                requestPermissions(REQUIRED_PERMISSIONS_CAMERA, REQUEST_CODE)
            } else {
                startCamera()
            }
        }

        viewBinding.cameraButton.setOnClickListener { takePhoto() }
        viewBinding.galleryButton.setOnClickListener{pickImageFromGallery()}

        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)


        return viewBinding.root
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

                    val quality: Int = 10
                    val compressedBitmap = compressBitmap(bitmap!!, targetSize, quality)

                    val tempFile = createTempFile()
                    saveBitmapToFile(compressedBitmap, tempFile)

                    val savedUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.ktl.bondoman.fileprovider",
                        tempFile
                    )

                    val validationFrag = ScanValidationFragment.newInstance(savedUri.toString(),true)
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(com.ktl.bondoman.R.id.nav_host_fragment, validationFrag)
                        ?.addToBackStack(null)
                        ?.commit()
                }

            }
        )
    }

    fun compressBitmap(bitmap: Bitmap, maxFileSize: Long, quality: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        var scaledBitmap = bitmap
//        var bytes = outputStream.toByteArray()
//        var tempQuality = quality
//        do {
//            outputStream.reset()
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, tempQuality, outputStream)
//            bytes = outputStream.toByteArray()
//            scaledBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//            tempQuality -= 10
//        } while (bytes.size > maxFileSize && tempQuality > 0)

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        scaledBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))

        return scaledBitmap
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE )
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK){
            data?.data?.let { uri ->
                val bitmap : Bitmap? = getBitmapFromUri(requireContext(), uri)

                val quality: Int = 10
                val compressedBitmap = compressBitmap(bitmap!!, targetSize, quality)

                val tempFile = createTempFile()
                saveBitmapToFile(compressedBitmap, tempFile)

                val savedUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.ktl.bondoman.fileprovider",
                    tempFile
                )

                val validationFrag = ScanValidationFragment.newInstance(savedUri.toString(),false)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(com.ktl.bondoman.R.id.nav_host_fragment, validationFrag)
                    ?.addToBackStack(null)
                    ?.commit()
            } ?: Toast.makeText(requireContext(), "Error: No image selected!", Toast.LENGTH_LONG).show()
        }
    }

    fun checkPermissions() : Boolean{
        val cameraPermissions = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val writePermissions = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val readPermissions = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return cameraPermissions && writePermissions && readPermissions
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

    private fun createTempFile(): File {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_temp_",
//            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            deleteOnExit()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Scan"
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
                    fragment.requestPermissions(REQUIRED_PERMISSIONS_CAMERA, REQUEST_CODE)
                    fragment.requestPermissions(REQUIRED_PERMISSIONS_STORAGE, REQUEST_CODE)
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