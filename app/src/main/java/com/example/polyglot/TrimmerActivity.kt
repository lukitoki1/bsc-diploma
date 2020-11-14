package com.example.polyglot

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImageView

class TrimmerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer)
        setImage()
    }

    fun onRejectButtonClick(view: View) {
        finish()
    }

    private fun setImage() {
        val imageURI = intent.getParcelableExtra<Uri>(PHOTO_URI)
        val trimmerCropView = findViewById<CropImageView>(R.id.trimmer_crop_view)
        trimmerCropView.setImageUriAsync(imageURI)
    }
}