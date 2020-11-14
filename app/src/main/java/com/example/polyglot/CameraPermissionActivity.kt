package com.example.polyglot

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CameraPermissionActivity : AppCompatActivity() {
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            if (!permissions.values.contains(false)) {
                startCamera()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_permission)
    }

    fun onStartCameraButtonClick(view: View) {
        cameraPermissionLauncher.launch(
            arrayOf(CAMERA_PERMISSION, WRITE_STORAGE_PERMISSION)
        )
    }

    private fun startCamera() {

    }
}