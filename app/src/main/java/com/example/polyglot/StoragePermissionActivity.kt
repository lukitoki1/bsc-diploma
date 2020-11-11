package com.example.polyglot

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class StoragePermissionActivity : AppCompatActivity() {
    private val storagePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startFilePicker()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_permission)
    }

    fun onStartFilePickerButtonClick(view: View) {
        storagePermissionLauncher.launch(READ_STORAGE_PERMISSION)
    }

    private fun startFilePicker() {

    }
}