package com.example.polyglot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main_menu.*

const val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
const val PHOTO_URI = "com.example.polyglot.PHOTO_URI"

class MainMenuActivity : AppCompatActivity() {
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
        checkCameraFeature()
    }

    fun onStartCameraButtonClick(view: View) {
        startActivity(Intent(applicationContext, CameraActivity::class.java))
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

    private fun startFilePicker() {
        chooseFile.launch(arrayOf("image/*"))
    }

    private fun startStoragePermission() {
        startActivity(Intent(applicationContext, StoragePermissionActivity::class.java))
    }

    private fun checkCameraFeature() {
        main_menu_take_photo_button.isEnabled = hasCameraFeature() ?: false
    }

    private fun hasCameraFeature(): Boolean? {
        return this.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

}