package com.example.polyglot

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
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
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.layout_results_list.*

private enum class ResultsViewFlipper(val value: Int) {
    EMPTY(1),
    LOADED(2)
}

class ResultsActivity : AppCompatActivity() {
    private lateinit var resultsAdapter: ResultsAdapter

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

        init()
        observe()
        setPhotoUri()
    }

    fun onNewTranslationButtonClick(view: View) {
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    }

    private fun setPhotoUri() {
        model.photoUri.value = intent.getParcelableExtra(PHOTO_URI)!!
    }

    private fun init() {
        initResultsAdapter()
        initLanguagesAdapter()
    }

    private fun initResultsAdapter() {
        resultsAdapter = ResultsAdapter(ArrayList())
        results_recycler_view.layoutManager = LinearLayoutManager(this)
        results_recycler_view.adapter = resultsAdapter
    }

    private fun initLanguagesAdapter() {
        val languagesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.item_language,
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

    private fun observe() {
        observePhotoUri()
        observeText()
        observeResults()
        observeTargetLanguage()
    }

    private fun observePhotoUri() {
        model.photoUri.observe(
            this,
            Observer { uri ->
                model.photo.value =
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
                recognizeText(this, uri)?.addOnSuccessListener { text ->
                    model.text.value = text
                }
            })
    }

    private fun observeText() {
        model.text.observe(this, Observer {
            showResults()
        })
    }

    private fun observeResults() {
        model.results.observe(
            this,
            Observer {
                when (it.size) {
                    0 -> results_view_flipper.displayedChild = ResultsViewFlipper.EMPTY.value
                    else -> results_view_flipper.displayedChild = ResultsViewFlipper.LOADED.value
                }
                resultsAdapter.update(it)
            })
    }

    private fun observeTargetLanguage() {
        model.targetLanguage.observe(this, Observer { showResults() })
    }

    private fun showResults() {
        model.clearResults()

        val photo = model.photo.value ?: return
        val targetLanguage = model.targetLanguage.value ?: return

        model.text.value?.also {
            for (textBlock in it.textBlocks) {
                val sourceTextWrapper = TextWrapper(
                    textBlock.text,
                    textBlock.recognizedLanguage
                )

                val box: Rect? = textBlock.boundingBox
                val sourceTextPhoto = box?.let {
                    try {
                        Bitmap.createBitmap(photo, box.left, box.top, box.width(), box.height())
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        null
                    }
                }

                val sourceLanguage = textBlock.recognizedLanguage

                downloadTranslatorModel(sourceLanguage, targetLanguage).addOnSuccessListener {
                    val sourceText = textBlock.text.replace("\n", " ")
                    translateText(sourceText, sourceLanguage, targetLanguage).addOnSuccessListener {
                        val targetTextWrapper = TextWrapper(it, targetLanguage)
                        val result = Result(sourceTextWrapper, targetTextWrapper, sourceTextPhoto)
                        model.appendResults(result)
                    }
                }
            }
        }
    }
}