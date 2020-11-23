package com.example.polyglot.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyglot.adapter.Result
import com.google.mlkit.vision.text.Text

class ResultsViewModel : ViewModel() {
    val targetLanguage: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val photoUri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val photo: MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }

    val text: MutableLiveData<Text> by lazy { MutableLiveData<Text>() }
    val results: MutableLiveData<ArrayList<Result>> by lazy { MutableLiveData<ArrayList<Result>>() }

    fun clearResults() {
        results.value = ArrayList()
    }

    fun appendResults(result: Result) {
        val currentResults: ArrayList<Result> = results.value ?: ArrayList()
        currentResults.add(result)
        results.value = currentResults
    }
}