package com.example.polyglot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_file_picker.*

const val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

class FilePickerActivity : AppCompatActivity() {
    private enum class Views(val value: Int) {
        LOADING(0),
        PERMISSION(1)
    }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> startFilePicker()
                else -> showPermissionRationale()
            }
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { documentUri ->
            if (documentUri != null) {
                startActivity(
                    Intent(this, TrimmerActivity::class.java).putExtra(PHOTO_URI, documentUri)
                )
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)
        checkPermission()
    }

    fun onGrantPermissionButtonClick(view: View) {
        storagePermissionLauncher.launch(READ_STORAGE_PERMISSION)
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                READ_STORAGE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startFilePicker()
            }
            shouldShowRequestPermissionRationale(READ_STORAGE_PERMISSION) -> {
                showPermissionRationale()
            }
            else -> {
                storagePermissionLauncher.launch(READ_STORAGE_PERMISSION)
            }
        }
    }

    private fun startFilePicker() {
        file_picker_view_flipper.displayedChild = Views.LOADING.value
        filePickerLauncher.launch(arrayOf("image/*"))
    }

    private fun showPermissionRationale() {
        file_picker_view_flipper.displayedChild = Views.PERMISSION.value
    }
}