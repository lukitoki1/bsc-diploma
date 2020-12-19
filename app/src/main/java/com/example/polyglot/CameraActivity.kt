package com.example.polyglot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.polyglot.utils.FileUtils
import kotlinx.android.synthetic.main.activity_camera.*

const val CAMERA_PERMISSION = Manifest.permission.CAMERA

class CameraActivity : AppCompatActivity() {
    private enum class Views(val value: Int) {
        LOADING(0),
        PERMISSION(1)
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> startCamera()
                else -> showPermissionRationale()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess && photoURI != null) {
                startActivity(
                    Intent(this, TrimmerActivity::class.java).putExtra(PHOTO_URI, photoURI)
                )
            }
            finish()
        }

    private var photoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        checkPermission()
    }

    fun onGrantPermissionButtonClick(view: View) {
        cameraPermissionLauncher.launch(CAMERA_PERMISSION)
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(CAMERA_PERMISSION) -> {
                showPermissionRationale()
            }
            else -> {
                cameraPermissionLauncher.launch(CAMERA_PERMISSION)
            }
        }
    }

    private fun startCamera() {
        camera_view_flipper.displayedChild = Views.LOADING.value
        photoURI = FileUtils.createFileUri(this)
        cameraLauncher.launch(photoURI)
    }

    private fun showPermissionRationale() {
        camera_view_flipper.displayedChild = Views.PERMISSION.value
    }
}