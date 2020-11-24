package com.example.polyglot

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polyglot.adapter.ResultsAdapter
import com.example.polyglot.utils.*
import com.example.polyglot.viewmodel.ResultsViewModel
import com.example.polyglot.viewmodel.TextData
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.layout_results_list.*

private enum class FlipperViews(val value: Int) {
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
            model.targetLanguage.value = availableLanguages[position]
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
        resultsAdapter = ResultsAdapter(this)
        results_recycler_view.layoutManager = LinearLayoutManager(this)
        results_recycler_view.adapter = resultsAdapter
    }

    private fun initLanguagesAdapter() {
        val languagesDescriptions = LocaleUtils.buildDescriptions(this, availableLanguages)
        val languagesAdapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.item_language, languagesDescriptions)
        results_languages_spinner.adapter = languagesAdapter
        results_languages_spinner.onItemSelectedListener = ResultLanguageSelectListener()
        results_languages_spinner.setSelection(availableLanguages.indexOf(TranslateLanguage.POLISH))
    }

    private fun observe() {
        observePhotoUri()
        observeText()
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
            when (it.textBlocks.size) {
                0 -> results_view_flipper.displayedChild = FlipperViews.EMPTY.value
                else -> {
                    results_view_flipper.displayedChild = FlipperViews.LOADED.value
                    val photo = model.photo.value ?: return@Observer
                    resultsAdapter.init(it, photo)
                    translateText()
                }
            }
        })
    }

    private fun observeTargetLanguage() {
        model.targetLanguage.observe(this, Observer {
            resultsAdapter.clearTranslations()
            translateText()
        })
    }

    private fun translateText() {
        val text = model.text.value ?: return
        val targetLanguage = model.targetLanguage.value ?: return

        for ((i, textBlock) in text.textBlocks.withIndex()) {
            val sourceLanguage = textBlock.recognizedLanguage

            if (sourceLanguage in arrayOf(targetLanguage, undefinedLanguage)) {
                resultsAdapter.setStateNoTranslation(i)
                continue
            }

            resultsAdapter.setStateFetchingModel(i)
            downloadTranslationModel(sourceLanguage, targetLanguage).addOnSuccessListener {
                resultsAdapter.setStateTranslating(i)
                translateText(textBlock.text, sourceLanguage, targetLanguage).addOnSuccessListener {
                    val targetTextData = TextData(it, targetLanguage)
                    resultsAdapter.setStateTranslated(i, targetTextData)
                }
            }
        }
    }
}