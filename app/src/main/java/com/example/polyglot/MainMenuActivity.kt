package com.example.polyglot

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

const val PHOTO_URI = "com.example.polyglot.PHOTO_URI"

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        checkCameraFeature()
    }

    fun onStartCameraButtonClick(view: View) {
        startActivity(Intent(applicationContext, CameraActivity::class.java))
    }

    fun onStartFilePickerButtonClick(view: View) {
        startActivity(Intent(applicationContext, FilePickerActivity::class.java))
    }

    private fun checkCameraFeature() {
        main_menu_take_photo_button.isEnabled = hasCameraFeature() ?: false
    }

    private fun hasCameraFeature(): Boolean? {
        return this.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

}