package com.example.polyglot

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polyglot.adapter.Result
import com.example.polyglot.adapter.ResultsAdapter
import com.example.polyglot.adapter.TextWrapper
import com.example.polyglot.utils.downloadTranslatorModel
import com.example.polyglot.utils.recognizeText
import com.example.polyglot.utils.translateText
import com.example.polyglot.viewmodel.ResultsViewModel
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.text.Text
import kotlinx.android.synthetic.main.activity_results.*


class ResultsActivity : AppCompatActivity() {
    private lateinit var resultsAdapter: ResultsAdapter
    private lateinit var photoUri: Uri
    private lateinit var photo: Bitmap

    private var text: Text? = null
    private var targetLanguage = TranslateLanguage.POLISH

    private val model: ResultsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        initialize()
        recognize()
    }

    private fun initialize() {
        photoUri = intent.getParcelableExtra(PHOTO_URI)!!
        photo = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, photoUri))

        resultsAdapter = ResultsAdapter(ArrayList())
        results_recycler_view.layoutManager = LinearLayoutManager(this)
        results_recycler_view.adapter = resultsAdapter

        model.results.observe(
            this,
            Observer<ArrayList<Result>> { newResults -> resultsAdapter.update(newResults) })

        Log.d("LANG", TranslateLanguage.getAllLanguages().toString())
    }

    private fun recognize() {
        recognizeText(this, photoUri)?.addOnSuccessListener { text ->
            this.text = text
            showResults()
        }
    }

    private fun showResults() {
        for (textBlock in text!!.textBlocks) {
            val sourceTextWrapper = TextWrapper(
                textBlock.text,
                textBlock.recognizedLanguage
            )

            val box: Rect? = textBlock.boundingBox
            val photo = box?.let {
                Bitmap.createBitmap(this.photo, box.left, box.top, box.width(), box.height())
            }

            val sourceLanguage = textBlock.recognizedLanguage
            val targetLanguage = targetLanguage
            downloadTranslatorModel(sourceLanguage, targetLanguage).addOnSuccessListener {
                val sourceText = textBlock.text.replace("\n", " ")
                translateText(sourceText, sourceLanguage, targetLanguage).addOnSuccessListener {
                    val targetTextWrapper = TextWrapper(it, targetLanguage)
                    val result = Result(sourceTextWrapper, targetTextWrapper, photo)
                    model.appendResults(result)
                }
            }
        }
    }
}