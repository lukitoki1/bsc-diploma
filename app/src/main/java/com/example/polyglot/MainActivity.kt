package com.example.polyglot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startMainMenu()
    }

    private fun startMainMenu() {
        Timer().schedule(1000) {
            startActivity(Intent(applicationContext, MainMenuActivity::class.java))
            finish()
        }
    }
}