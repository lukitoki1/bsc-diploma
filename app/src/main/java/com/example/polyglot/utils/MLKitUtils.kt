package com.example.polyglot.utils

import android.graphics.Bitmap
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

fun recognizeText(photoBitmap: Bitmap?, onSuccess: (Text) -> Unit) {
    var text: Text? = null
    photoBitmap ?: return

    val image = InputImage.fromBitmap(photoBitmap, 0)
    val recognizer = TextRecognition.getClient()
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            onSuccess(visionText)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}

fun translateTextBlock(textBlock: Text.TextBlock, targetLanguage: String): String? {
    val sourceLanguage = TranslateLanguage.fromLanguageTag(textBlock.recognizedLanguage)
        ?: return null

    val options = TranslatorOptions.Builder()
        .setSourceLanguage(sourceLanguage)
        .setTargetLanguage(targetLanguage)
        .build()
    val translator = Translation.getClient(options)

    var text: String? = null
    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    translator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {
            translator.translate(textBlock.text)
                .addOnSuccessListener { translatedText ->
                    text = translatedText
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
    return text
}

