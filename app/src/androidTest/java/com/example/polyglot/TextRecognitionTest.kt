package com.example.polyglot

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.polyglot.utils.TaskUtils
import com.example.polyglot.utils.recognizeText
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import info.debatty.java.stringsimilarity.*;

const val DEBUG_TAG = "ML_TEST"

private data class TestResult(
    val bitmap: Bitmap,
    var name: String = "",
    var expectedText: String = "",
    var actualText: String = "",
    var similarity: Double = 0.0
)

@RunWith(AndroidJUnit4::class)
class TextRecognitionTest {
    private val filesRoot = "text_recognition"
    private val photosFolders = arrayListOf(
        "SceneTrialTest/ryoungt_05.08.2002",
        "SceneTrialTest/ryoungt_13.08.2002",
        "SceneTrialTest/sml_01.08.2002"
    )
    private val descriptionFile = "words.txt"

    private lateinit var testContext: Context
    private lateinit var assetManager: AssetManager

    @Before
    fun setUp() {
        testContext = InstrumentationRegistry.getInstrumentation().context
        assetManager = testContext.assets
    }

    @Test
    fun testTextRecognition() {
        val results = initTestResultsWithBitmaps().take(20)
        constructExpectedText(results)
        recognizeActualText(results)
        calculateCosineSimilarity(results)
        saveResultsToExcel(results)
    }

    private fun initTestResultsWithBitmaps(): List<TestResult> {
        val photos = ArrayList<TestResult>()
        val photosFoldersPaths = photosFolders.map { "$filesRoot/$it" }
        for (folderPath in photosFoldersPaths) {
            val photosNames = assetManager.list(folderPath) ?: continue
            for (photoName in photosNames) {
                val photoPath = "$folderPath/$photoName"
                val testInput = assetManager.open(photoPath)

                val bitmap = BitmapFactory.decodeStream(testInput)
                Log.d(DEBUG_TAG, "Decoded photo $photoPath")
                photos.add(TestResult(bitmap = bitmap, name = photoPath))
            }
        }
        return photos
    }

    private fun constructExpectedText(results: List<TestResult>) {
        val wordsInput = assetManager.open("$filesRoot/$descriptionFile")
        val wordsLines = wordsInput.bufferedReader().use { it.readText() }.lines()
        for (i in results.indices) {
            results[i].expectedText = wordsLines[i]
        }
    }

    private fun recognizeActualText(results: List<TestResult>) {
        for (i in results.indices) {
            val task = recognizeText(results[i].bitmap)
            await().until(TaskUtils.isTaskComplete(task))
            results[i].actualText = task?.result?.text?.replace("\n", " ")?.replace("\t", " ")?.trim() ?: ""
            Log.d(DEBUG_TAG, "Recognized in photo no. $i: \"${results[i].actualText}\"")
        }
    }

    private fun calculateCosineSimilarity(results: List<TestResult>) {
        val cosine = Cosine()
        for (i in results.indices) {
            results[i].similarity = cosine.similarity(results[i].expectedText, results[i].actualText)
        }
    }

    private fun saveResultsToExcel(results: List<TestResult>) {
        for(i in results.indices){
            val r = results[i]
            Log.d(DEBUG_TAG, "Final result: ${r.name} | ${r.expectedText} | ${r.actualText} | ${r.similarity}")
        }
    }
}