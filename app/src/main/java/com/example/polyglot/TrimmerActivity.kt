package com.example.polyglot

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImageView

class TrimmerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer)
        setImage()
    }

    private fun setImage() {
        val bitmap = intent.getParcelableExtra<Bitmap>(PHOTO_PREVIEW)
        val trimmerCropView = findViewById<CropImageView>(R.id.trimmer_crop_view)
        trimmerCropView.setImageBitmap(bitmap)
    }
}