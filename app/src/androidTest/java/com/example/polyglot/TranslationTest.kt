package com.example.polyglot

import android.Manifest
import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.example.polyglot.utils.*
import info.debatty.java.stringsimilarity.Cosine
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

private data class TranslationTestResult(
    var originalText: String = "",
    var expectedTranslatedText: String = "",
    var expectedTranslatedTextMap: MutableMap<String, Int> = mutableMapOf(),
    var actualTranslatedText: String = "",
    var actualTranslatedTextMap: MutableMap<String, Int> = mutableMapOf(),
    var similarity: Double = 0.0
)

@RunWith(AndroidJUnit4::class)
class TranslationTest {
    private val filesRoot = "translation"
    private val sourceFile = "German-English_WordAlignment/test.de"
    private val targetFile = "words.en"

    private val sourceLanguage = "de"
    private val targetLanguage = "en"

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var cosine: Cosine
    private lateinit var results: ArrayList<TranslationTestResult>

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        assetManager = context.assets
        cosine = Cosine()
        results = ArrayList()
    }

    @Test
    fun testTranslation() {
        loadOriginalText()
        loadExpectedTranslation()
        translateOriginalText()
        calculateCosineSimilarity()
        saveResultsToExcel()
    }

    private fun loadOriginalText() {
        val originalTextFile = assetManager.open("$filesRoot/$sourceFile")
        val originalTextLines = InputStreamUtils.readLines(originalTextFile)
        for (line in originalTextLines) {
            results.add(TranslationTestResult(originalText = line))
            Log.d(DEBUG_TAG, "Loaded original text: \"$line\"")
        }
    }

    private fun loadExpectedTranslation() {
        val translatedTextFile = assetManager.open("$filesRoot/$targetFile")
        val translatedTextLines = InputStreamUtils.readLines(translatedTextFile)
        for (i in results.indices) {
            val translatedText = translatedTextLines[i]
            results[i].expectedTranslatedText = translatedText
            results[i].expectedTranslatedTextMap = StatUtils.countWords(translatedText)
            Log.d(DEBUG_TAG, "Loaded translated text: \"$translatedText\"")
        }
    }

    private fun translateOriginalText() {
        val modelDownloadTask = downloadTranslationModel(sourceLanguage, targetLanguage)
        await().until(TaskUtils.isTaskComplete(modelDownloadTask))
        Log.d(DEBUG_TAG, "Downloaded $sourceLanguage-$targetLanguage translation model")

        for (i in results.indices) {
            val translationTask = translateText(results[i].originalText, sourceLanguage, targetLanguage)
            await().until(TaskUtils.isTaskComplete(translationTask))
            val translatedText = translationTask.result
                .toLowerCase()
                .replace("[.,:;!?']".toRegex(), "")
                .replace("'s", "")
                .replace(" s ", " ")
                .replace("  ", " ")
            results[i].actualTranslatedText = translatedText
            results[i].actualTranslatedTextMap = StatUtils.countWords(translatedText)
            Log.d(DEBUG_TAG, "Translated text: $translatedText")
        }
    }

    private fun calculateCosineSimilarity() {
        for (i in results.indices) {
            results[i].similarity = StatUtils.calculateCosine(
                cosine,
                results[i].expectedTranslatedTextMap,
                results[i].actualTranslatedTextMap
            )
        }
    }

    private fun saveResultsToExcel() {
        val excelWorkbook = XSSFWorkbook()
        val excelWorksheet = excelWorkbook.createSheet("test_results")

        val row = excelWorksheet.createRow(0)
        row.createCell(0).setCellValue("original_text")
        row.createCell(1).setCellValue("expected_translated_text")
        row.createCell(2).setCellValue("actual_translated_text")
        row.createCell(3).setCellValue("cosine_similarity")

        for (i in results.indices) {
            val result = results[i]
            val resultRow = excelWorksheet.createRow(i + 1)

            resultRow.createCell(0).setCellValue(result.originalText)
            resultRow.createCell(1).setCellValue(result.expectedTranslatedText)
            resultRow.createCell(2).setCellValue(result.actualTranslatedText)
            resultRow.createCell(3).setCellValue(result.similarity)

            Log.d(
                DEBUG_TAG,
                "Final result: ${result.originalText} | ${result.expectedTranslatedText} | ${result.actualTranslatedText} | ${result.similarity}"
            )
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$filesRoot.xlsx")
        val outputStream = FileOutputStream(file)
        excelWorkbook.write(outputStream)
        excelWorkbook.close()
    }


}