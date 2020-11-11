package com.example.polyglot

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CameraPermissionActivity : AppCompatActivity() {
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_permission)
    }

    fun startCamera(view: View) {
        cameraPermissionLauncher.launch(
            CAMERA_PERMISSION
        )
    }

    private fun startCamera() {

    }
}