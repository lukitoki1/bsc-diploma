package com.example.polyglot

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.polyglot.utils.TaskUtils
import com.example.polyglot.utils.recognizeText
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import info.debatty.java.stringsimilarity.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Rule
import java.io.File
import androidx.test.rule.GrantPermissionRule
import com.example.polyglot.utils.InputStreamUtils
import com.example.polyglot.utils.StatUtils
import java.io.FileOutputStream

const val DEBUG_TAG = "ML_TEST"

private data class TextRecognitionTestResult(
    val bitmap: Bitmap,
    var name: String = "",
    var expectedText: String = "",
    var actualText: String = "",
    var expectedMap: MutableMap<String, Int> = mutableMapOf(),
    var actualMap: MutableMap<String, Int> = mutableMapOf(),
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

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var cosine: Cosine
    private lateinit var results: ArrayList<TextRecognitionTestResult>

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        context = getInstrumentation().context
        assetManager = context.assets
        cosine = Cosine()
        results = ArrayList()
    }

    @Test
    fun testTextRecognition() {
        loadBitmaps()
        loadExpectedText()
        recognizeActualText()
        calculateCosineSimilarity()
        saveResultsToExcel()
    }

    private fun loadBitmaps() {
        val photosFoldersPaths = photosFolders.map { "$filesRoot/$it" }
        for (folderPath in photosFoldersPaths) {
            val photosNames = assetManager.list(folderPath) ?: continue
            for (photoName in photosNames) {
                val photoPath = "$folderPath/$photoName"
                val testInput = assetManager.open(photoPath)
                val bitmap = BitmapFactory.decodeStream(testInput)
                Log.d(DEBUG_TAG, "Decoded photo $photoPath")
                results.add(TextRecognitionTestResult(bitmap = bitmap, name = photoPath))
            }
        }
    }

    private fun loadExpectedText() {
        val wordsInput = assetManager.open("$filesRoot/$descriptionFile")
        val wordsLines = InputStreamUtils.readLines(wordsInput)
        for (i in results.indices) {
            val text = wordsLines[i]
            results[i].expectedText = text
            results[i].expectedMap = StatUtils.countWords(text)
            Log.d(DEBUG_TAG, "Loaded text: \"$text\"")
        }
    }

    private fun recognizeActualText() {
        for (i in results.indices) {
            val task = recognizeText(results[i].bitmap)
            await().until(TaskUtils.isTaskComplete(task))

            val text = task?.result?.text?.replace("\n", " ")?.replace("\t", " ")?.trim() ?: ""
            results[i].actualText = text
            results[i].actualMap = StatUtils.countWords(text)

            Log.d(DEBUG_TAG, "Recognized in photo no. $i: \"${results[i].actualText}\"")
        }
    }

    private fun calculateCosineSimilarity() {
        for (i in results.indices) {
            results[i].similarity = StatUtils.calculateCosine(cosine, results[i].expectedMap, results[i].actualMap)
        }
    }

    private fun saveResultsToExcel() {
        val excelWorkbook = XSSFWorkbook()
        val excelWorksheet = excelWorkbook.createSheet("test_results")

        val row = excelWorksheet.createRow(0)
        row.createCell(0).setCellValue("name")
        row.createCell(1).setCellValue("expected_text")
        row.createCell(2).setCellValue("actual_text")
        row.createCell(3).setCellValue("cosine_similarity")

        for (i in results.indices) {
            val result = results[i]
            val resultRow = excelWorksheet.createRow(i + 1)

            resultRow.createCell(0).setCellValue(result.name)
            resultRow.createCell(1).setCellValue(result.expectedText)
            resultRow.createCell(2).setCellValue(result.actualText)
            resultRow.createCell(3).setCellValue(result.similarity)

            Log.d(
                DEBUG_TAG,
                "Final result: ${result.name} | ${result.expectedText} | ${result.actualText} | ${result.expectedMap} | ${result.actualMap} | ${result.similarity}"
            )
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$filesRoot.xlsx")
        val outputStream = FileOutputStream(file)
        excelWorkbook.write(outputStream)
        excelWorkbook.close()
    }
}