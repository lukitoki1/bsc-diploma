package com.example.polyglot.utils

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

fun recognizeText(context: Context, photoUri: Uri?): Task<Text>? {
    photoUri ?: return null

    val image = InputImage.fromFilePath(context, photoUri)
    val recognizer = TextRecognition.getClient()
    return recognizer.process(image)
}

fun translateText(text: String, sourceLanguage: String, targetLanguage: String): Task<String> {
    val translator = buildTranslator(sourceLanguage, targetLanguage)
    return translator.translate(text).addOnCompleteListener { translator.close() }
}

fun downloadTranslatorModel(sourceLanguage: String, targetLanguage: String): Task<Void> {
    val translator = buildTranslator(sourceLanguage, targetLanguage)
    return translator.downloadModelIfNeeded().addOnCompleteListener { translator.close() }
}

private fun buildTranslator(sourceLanguage: String, targetLanguage: String): Translator {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(sourceLanguage)
        .setTargetLanguage(targetLanguage)
        .build()
    return Translation.getClient(options)
}

