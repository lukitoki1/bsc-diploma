package com.example.polyglot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import createFileUri
import kotlinx.android.synthetic.main.activity_main_menu.*

const val CAMERA_PERMISSION = Manifest.permission.CAMERA
const val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
const val WRITE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

const val PHOTO_URI = "com.example.polyglot.PHOTO_URI"

class MainMenuActivity : AppCompatActivity() {
    private var pm: PackageManager? = null
    private var hasCamera: Boolean = false
    private var photoURI: Uri? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            if (permissions.values.contains(false)) {
                startCameraPermission()
            } else {
                startCamera()
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
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess && photoURI != null) {
                Log.d("FILES", "takePicture file URI: $photoURI")
                startActivity(
                    Intent(this, TrimmerActivity::class.java).putExtra(PHOTO_URI, photoURI)
                )
            }
        }

    private val chooseFile =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { documentUri ->
            if (documentUri != null) {
                Log.d("FILES", "chooseFile file URI: $documentUri")
                startActivity(
                    Intent(this, TrimmerActivity::class.java).putExtra(PHOTO_URI, documentUri)
                )
            }
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
            cameraPermissionLauncher.launch(arrayOf(CAMERA_PERMISSION, WRITE_STORAGE_PERMISSION))
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
        photoURI = createFileUri(this)
        takePicture.launch(photoURI)
    }

    private fun startCameraPermission() {
        startActivity(Intent(applicationContext, CameraPermissionActivity::class.java))
    }

    private fun startFilePicker() {
        chooseFile.launch(arrayOf("image/*"))
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
        main_menu_take_photo_button.isEnabled = this.hasCamera
    }

}