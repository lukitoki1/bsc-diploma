package com.example.polyglot.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyglot.adapter.Result

class ResultsViewModel : ViewModel() {
    val results: MutableLiveData<ArrayList<Result>> by lazy { MutableLiveData<ArrayList<Result>>() }

    fun appendResults(result: Result) {
        val currentResults: ArrayList<Result> = results.value ?: ArrayList()
        currentResults.add(result)
        results.value = currentResults
    }
}