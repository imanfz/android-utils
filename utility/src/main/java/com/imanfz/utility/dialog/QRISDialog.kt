package com.imanfz.utility.dialog

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.imanfz.utility.R
import com.imanfz.utility.base.BaseDialogFragment
import com.imanfz.utility.databinding.FragmentQrisDialogBinding
import com.imanfz.utility.extension.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by Iman Faizal on 31/Aug/2022
 **/

@Suppress("unused")
class QRISDialog(
    private val onResult: ((value: String) -> Unit)
): BaseDialogFragment<FragmentQrisDialogBinding>() {

    private val listPermission = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var cameraPermissionLauncher : ActivityResultLauncher<String>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var resultLauncherGallery: ActivityResultLauncher<Intent>
    private lateinit var codeScanner: CodeScanner

    companion object {

        @JvmStatic
        fun newInstance(
            onResult: ((value: String) -> Unit)
        ) = QRISDialog(onResult)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupResultLauncher()
        setupView()
        setupListener()
    }

    override fun onDestroyView() {
        codeScanner.apply {
            stopPreview()
            decodeCallback = null
            errorCallback = null
        }
        cameraPermissionLauncher.unregister()
        permissionResultLauncher.unregister()
        resultLauncherGallery.unregister()
        // handle leak fro CodeScannerView
        binding.apply {
            root.removeView(scannerView)
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun getTheme(): Int = R.style.DialogFullscreenTheme

    private fun setupResultLauncher() {
        permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Handle Permission granted/rejected
            if (permissions.entries.all { it.value }) {
                openGallery()
            } else requireContext().showBasicDialog(
                getString(R.string.failed),
                getString(R.string.read_write_required)
            )
        }
        cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permissions ->
            // Handle Permission granted/rejected
            if (permissions) {
                codeScanner.startPreview()
            } else {
                requireContext().showBasicDialog(
                    getString(R.string.failed),
                    getString(R.string.camera_required),
                    okClicked = {
                        dismiss()
                    }
                )
            }
        }
        resultLauncherGallery = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val data = result.data
                    val imageUri = data!!.data!!
                    val imagePath = convertMediaUriToPath(imageUri)
                    val imgFile = File(imagePath)
                    scanImageQRCode(imgFile)
                } catch(error: Exception) {
                    requireContext().shortToast(error.message ?: getString(R.string.error_please_try_again))
                }
            } else {
                requireContext().shortToast(getString(R.string.result_not_found))
            }
        }
    }

    override fun setupView() {
        isCancelable = true
        binding.apply {
            codeScanner = CodeScanner(requireActivity(), scannerView).apply {
                formats = listOf(BarcodeFormat.QR_CODE) // qr only
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                setDecodeCallback {
                    requireActivity().runOnUiThread {
                        onResult(it.text)
                        dismiss()
                    }
                }
                setErrorCallback {
                    requireActivity().runOnUiThread {
                        if (!requireContext().hasPermission(Manifest.permission.CAMERA)) {
                            showAlert()
                        } else {
                            requireContext().shortToast(
                                it.message ?: getString(R.string.error_please_try_again)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun setupListener() {
        binding.apply {
            btnGallery.setSafeOnClickListener {
                if(requireContext().hasPermission(listPermission.toTypedArray())) {
                    openGallery()
                } else {
                    requestGalleryPermissions()
                }
            }
        }
    }

    private fun deniedPermission(): String {
        for (permission in listPermission) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }

    // Show alert dialog to request permissions
    private fun showAlert() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.need_permission))
            setMessage(getString(R.string.camera_required))
            setPositiveButton(getString(R.string.ok)) { al, _ ->
                al.dismiss()
                requestCameraPermissions()
            }
            setNegativeButton(getString(R.string.cancel)) { al, _ ->
                al.dismiss()
                this@QRISDialog.dismissAllowingStateLoss()
            }
            create()
        }.show()
    }

    // Request the permissions at run time
    private fun requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Show an explanation asynchronously
            requireContext().shortToast("Should show an explanation.")
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestGalleryPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), deniedPermission())) {
            // Show an explanation asynchronously
            requireContext().shortToast("Should show an explanation.")
        } else {
            permissionResultLauncher.launch(listPermission.toTypedArray())
        }
    }

    private fun openGallery() {
        resultLauncherGallery.launch(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )
    }

    private fun convertMediaUriToPath(uri: Uri):String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, proj, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path = cursor.getString(columnIndex)
        cursor.close()
        return path
    }

    private fun scanImageQRCode(file: File){
        val inputStream: InputStream = BufferedInputStream(FileInputStream(file))
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val decoded = scanQRImage(bitmap)
        logi("Decoded string = $decoded")
    }

    private fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            contents = result.text
            requireContext().shortToast("QR: " + result.text)
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            loge("Error decoding QR Code ${e.localizedMessage}")
            requireContext().longToast(getString(R.string.error_decoding_qr))
        }
        return contents
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.commit(true) {
                setReorderingAllowed(true)
                add(this@QRISDialog, tag ?: this@QRISDialog.TAG)
                addToBackStack(null)
            }
        } catch (ignored: IllegalStateException) {
            loge("show: ${ignored.localizedMessage}" )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            childFragmentManager.apply {
                if (backStackEntryCount > 0) popBackStack()
            }
            parentFragmentManager.apply {
                val fragment = findFragmentByTag(this@QRISDialog.TAG)
                if (fragment != null) {
                    commit(true) {
                        remove(fragment)
                    }
                    if (backStackEntryCount > 0) popBackStack()
                }
            }
        } catch (ignored: IllegalStateException) {
            loge("onDismiss: ${ignored.localizedMessage}" )
        }
    }
}

/**
 * With MLKit
 **/
/*class QRISDialog: DialogFragment() {

    val fTAG = "QRISDialog"
    private var _binding: FragmentQrisDialogBinding? = null
    private val binding get() = _binding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeBoxView: BarcodeBoxView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        isCancelable = false
        _binding = FragmentQrisDialogBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeBoxView = BarcodeBoxView(requireContext())
        setupView()
        setupListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun setupView() {
        if (requireContext().hasPermission(Manifest.permission.CAMERA)) {
            // startCamera
            startCamera()
        } else {
            permReqLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupListener() {
        binding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private val permReqLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            // Good pass
            if (requireContext().hasPermission(Manifest.permission.CAMERA)) {
                // start camera

            } else {
                requireContext().shortToast("Permission Denied")
                Log.e("QRIS", "no camera permission")
                dismiss()
            }
        } else {
            // Failed pass
            Log.e("QRIS", "no camera permission")
            // Permission denied
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Permission required")
                .setMessage("This application needs to access the camera to process barcodes")
                .setPositiveButton("Ok") { _, _ ->
                    // Keep asking for permission until granted
                    setupView()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    dismiss()
                    this.dismiss()
                }
                .setCancelable(false)
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                    show()
                }
        }
    }

    private fun startCamera() {
        binding?.apply {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                // Image analyzer
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutor,
                            QrCodeAnalyzer(
                                requireContext(),
                                barcodeBoxView,
                                previewView.width.toFloat(),
                                previewView.height.toFloat()
                            )
                        )
                    }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                    )

                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        }
    }
}

class QrCodeAnalyzer(
    private val context: Context,
    private val barcodeBoxView: BarcodeBoxView,
    private val previewViewWidth: Float,
    private val previewViewHeight: Float
) : ImageAnalysis.Analyzer {

    *//**
     * This parameters will handle preview box scaling
     *//*
    private var scaleX = 1f
    private var scaleY = 1f

    private fun translateX(x: Float) = x * scaleX
    private fun translateY(y: Float) = y * scaleY

    private fun adjustBoundingRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            // Update scale factors
            scaleX = previewViewWidth / img.height.toFloat()
            scaleY = previewViewHeight / img.width.toFloat()

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            // Process image searching for barcodes
            val options = BarcodeScannerOptions.Builder()
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        for (barcode in barcodes) {
                            // Handle received barcodes...
                            Toast.makeText(
                                context,
                                "Value: " + barcode.rawValue,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            // Update bounding rect
                            barcode.boundingBox?.let { rect ->
                                barcodeBoxView.setRect(
                                    adjustBoundingRect(
                                        rect
                                    )
                                )
                            }
                        }
                    } else {
                        // Remove bounding rect
                        barcodeBoxView.setRect(RectF())
                    }
                }
                .addOnFailureListener { }
        }

        image.close()
    }
}

class BarcodeBoxView(
    context: Context
) : View(context) {

    private val paint = Paint()

    private var mRect = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cornerRadius = 10f

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f

        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
    }

    fun setRect(rect: RectF) {
        mRect = rect
        invalidate()
        requestLayout()
    }
}*/
