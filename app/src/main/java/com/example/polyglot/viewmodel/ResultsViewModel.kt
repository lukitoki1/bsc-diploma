package com.example.polyglot.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text

enum class ResultState {
    INITIAL, FETCHING_MODEL, TRANSLATING, TRANSLATED
}

data class TextData(val text: String, val language: String)
data class Result(
    val photo: Bitmap?,
    val source: TextData,
    var target: TextData?,
    var state: ResultState = ResultState.INITIAL
)

class ResultsViewModel : ViewModel() {
    val targetLanguage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val photoUri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val photo: MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    val text: MutableLiveData<Text> by lazy { MutableLiveData<Text>() }


}