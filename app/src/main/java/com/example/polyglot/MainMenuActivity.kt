package com.example.polyglot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

const val CAMERA_PERMISSION = Manifest.permission.CAMERA
const val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
const val PHOTO_PREVIEW = "com.example.polyglot.PHOTO_PREVIEW"

class MainMenuActivity : AppCompatActivity() {
    private var pm: PackageManager? = null
    private var hasCamera: Boolean = false

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                startCameraPermission()
            }
        }

    private val storagePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startFilePicker()
            } else {
                startStoragePermission()
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            startActivity(Intent(this, TrimmerActivity::class.java).putExtra(PHOTO_PREVIEW, bitmap))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        initialize()
    }

    fun onStartCameraButtonClick(view: View) {
        if (!this.hasCamera) {
            return
        } else if (
            ContextCompat.checkSelfPermission(
                this,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else if (shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
            startCameraPermission()
        } else {
            cameraPermissionLauncher.launch(CAMERA_PERMISSION)
        }
    }

    fun onStartFilePickerButtonClick(view: View) {
        if (
            ContextCompat.checkSelfPermission(
                this,
                READ_STORAGE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startFilePicker()
        } else if (shouldShowRequestPermissionRationale(READ_STORAGE_PERMISSION)) {
            startStoragePermission()
        } else {
            storagePermissionLauncher.launch(READ_STORAGE_PERMISSION)
        }
    }

    private fun startCamera() {
        takePicture.launch(null)
    }

    private fun startCameraPermission() {
        startActivity(Intent(applicationContext, CameraPermissionActivity::class.java))
    }

    private fun startFilePicker() {

    }

    private fun startStoragePermission() {
        startActivity(Intent(applicationContext, StoragePermissionActivity::class.java))
    }

    private fun initialize() {
        this.pm = this.packageManager
        this.hasCamera = hasCameraFeature() ?: false
        toggleStartCameraButtonEnabled()
    }

    private fun hasCameraFeature(): Boolean? {
        return this.pm?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun toggleStartCameraButtonEnabled() {
        val takePhotoButton = findViewById<Button>(R.id.main_menu_take_photo_button)
        takePhotoButton.isEnabled = this.hasCamera
    }

}