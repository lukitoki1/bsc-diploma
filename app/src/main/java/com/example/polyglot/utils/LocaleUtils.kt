package com.example.polyglot.utils

import android.content.Context
import com.example.polyglot.R
import java.util.*

abstract class LocaleUtils {
    companion object {
        fun buildDescriptions(context: Context, languageCodes: List<String>) =
            languageCodes.map { buildDescription(context, it) }

        fun buildDescription(context: Context, languageCode: String): String {
            if (languageCode == undefinedLanguage) {
                return context.getString(R.string.unrecognized)
            }
            return Locale(languageCode).displayName
        }
    }
}