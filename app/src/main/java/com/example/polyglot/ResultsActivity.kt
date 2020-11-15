package com.example.polyglot

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polyglot.utils.recognizeText
import com.example.polyglot.utils.translateTextBlock
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.text.Text
import kotlinx.android.synthetic.main.activity_results.*


class ResultsActivity : AppCompatActivity() {
    private lateinit var resultsAdapter: ResultsAdapter

    private var photoUri: Uri? = null
    private var photo: Bitmap? = null
    private var textBlocks: List<Text.TextBlock>? = null
    private val targetLanguage = TranslateLanguage.POLISH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        initialize()
        recognize()
    }

    private fun initialize() {
        photoUri = intent.getParcelableExtra<Uri>(PHOTO_URI)
        if (photoUri != null) {
            photo =
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        this.contentResolver,
                        photoUri!!
                    )
                )
        }

        resultsAdapter = ResultsAdapter(ArrayList())
        results_recycler_view.layoutManager = LinearLayoutManager(this)
        results_recycler_view.adapter = resultsAdapter

        Log.d("LANG", TranslateLanguage.getAllLanguages().toString())
    }

    private fun recognize() {
        recognizeText(photo, ::onRecognitionSuccessful)
    }

    private fun onRecognitionSuccessful(text: Text) {
        textBlocks = text.textBlocks
        showResults()
    }

    private fun showResults() {
        val results: ArrayList<Result> = ArrayList()
        textBlocks ?: return

        for (textBlock in textBlocks!!) {
            var targetTextWrapper: TextWrapper? = null
            val targetText = translateTextBlock(
                textBlock,
                targetLanguage
            )
            if (targetText != null) {
                targetTextWrapper = TextWrapper(targetText, targetLanguage)
            }

            val sourceTextWrapper = TextWrapper(textBlock.text, textBlock.recognizedLanguage)

            var photo: Bitmap? = null
            if (textBlock.boundingBox != null) {
                val box: Rect = textBlock.boundingBox!!
                photo =
                    Bitmap.createBitmap(this.photo!!, box.left, box.top, box.width(), box.height())
            }
            results.add(Result(sourceTextWrapper, targetTextWrapper, photo))
        }

        resultsAdapter.update(results)
    }
}