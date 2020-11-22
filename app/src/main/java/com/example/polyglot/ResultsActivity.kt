package com.example.polyglot

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polyglot.adapter.Result
import com.example.polyglot.adapter.ResultsAdapter
import com.example.polyglot.adapter.TextWrapper
import com.example.polyglot.utils.*
import com.example.polyglot.viewmodel.ResultsViewModel
import com.google.mlkit.vision.text.Text
import kotlinx.android.synthetic.main.activity_results.*


class ResultsActivity : AppCompatActivity() {
    private lateinit var resultsAdapter: ResultsAdapter
    private lateinit var photoUri: Uri
    private lateinit var photo: Bitmap

    private var text: Text? = null
    private val model: ResultsViewModel by viewModels()

    inner class ResultLanguageSelectListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            model.targetLanguage.value = availableTargetLanguages[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        initPhoto()
        initResultsAdapter()
        initLanguagesAdapter()
        initRecognition()

        recognizeText()
    }

    fun onNewTranslationButtonClick(view: View) {
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    }

    private fun initPhoto() {
        photoUri = intent.getParcelableExtra(PHOTO_URI)!!
        photo = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, photoUri))
    }

    private fun initResultsAdapter() {
        resultsAdapter = ResultsAdapter(ArrayList())
        results_recycler_view.layoutManager = LinearLayoutManager(this)
        results_recycler_view.adapter = resultsAdapter

        model.results.observe(
            this,
            Observer<ArrayList<Result>> { newResults -> resultsAdapter.update(newResults) })
    }

    private fun initLanguagesAdapter() {
        val languagesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.listitem_language_dropdown,
            availableTargetLanguages
        )
        results_languages_spinner.adapter = languagesAdapter
        results_languages_spinner.onItemSelectedListener = ResultLanguageSelectListener()
        results_languages_spinner.setSelection(
            availableTargetLanguages.indexOf(
                defaultTargetLanguage
            )
        )
    }

    private fun initRecognition() {
        model.targetLanguage.observe(this, Observer { newTargetLanguage ->
            showResults()
        })
    }

    private fun recognizeText() {
        recognizeText(this, photoUri)?.addOnSuccessListener { text ->
            this.text = text
            showResults()
        }
    }

    private fun showResults() {
        model.clearResults()

        text?.also {
            for (textBlock in it.textBlocks) {
                val sourceTextWrapper = TextWrapper(
                    textBlock.text,
                    textBlock.recognizedLanguage
                )

                val box: Rect? = textBlock.boundingBox
                val photo = box?.let {
                    Bitmap.createBitmap(this.photo, box.left, box.top, box.width(), box.height())
                }

                val sourceLanguage = textBlock.recognizedLanguage
                val targetLanguage = model.targetLanguage.value ?: return

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
}