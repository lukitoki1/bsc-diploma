package com.example.polyglot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImageView
import createFileUri

class TrimmerActivity : AppCompatActivity() {
    var originalPhotoUri: Uri? = null
    var croppedPhotoUri: Uri? = null
    var trimmerCropView: CropImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer)
        initialize()
    }

    fun onRejectButtonClick(view: View) {
        finish()
    }

    fun onAcceptButtonClick(view: View) {
        croppedPhotoUri = createFileUri(this).also {
            trimmerCropView?.saveCroppedImageAsync(it)
        }
    }

    private fun initialize() {
        originalPhotoUri = intent.getParcelableExtra(PHOTO_URI)
        trimmerCropView = findViewById(R.id.trimmer_crop_view)
        trimmerCropView?.setImageUriAsync(originalPhotoUri)
        trimmerCropView?.setOnCropImageCompleteListener { _, _ -> startResults() }
    }

    private fun startResults() {
        startActivity(
            Intent(this, ResultsActivity::class.java).putExtra(PHOTO_URI, croppedPhotoUri)
        )
    }
}