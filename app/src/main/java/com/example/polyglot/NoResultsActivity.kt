package com.example.polyglot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NoResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_results_empty)
    }
}