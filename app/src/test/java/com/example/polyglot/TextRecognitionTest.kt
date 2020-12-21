package com.example.polyglot

import android.graphics.BitmapFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class TextRecognitionTest {

    @Test
    fun test2() {
        val url = javaClass.getResource("/text_recognition")
        val path = url?.path ?: return
        val files = File(path).listFiles() ?: return

        for (file in files) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            println(file.name)
            println(bitmap.width)
            println(bitmap.height)
        }
    }
}