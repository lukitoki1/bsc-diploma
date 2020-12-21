package com.example.polyglot

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.polyglot.utils.recognizeText
import kotlinx.coroutines.sync.Mutex
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.locks.ReentrantLock


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.polyglot", appContext.packageName)
    }

    @Test
    fun test2() {
        val root = "text_recognition"

        Log.d("AAAA", "BBBB")
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val assetManager: AssetManager = testContext.assets
        val names = assetManager.list(root) ?: return
        for (name in names) {
            val testInput = assetManager.open("$root/$name")
            val bitmap = BitmapFactory.decodeStream(testInput)
            Log.d("ML_KIT_TEST", name)
            val task = recognizeText(bitmap) ?: continue
            while(!task.isComplete) {
            }
            Log.d(
                "ML_KIT_TEST",
                "\"${task.result.text}\""
            )
        }
    }
}