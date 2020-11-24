package com.example.polyglot.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.text.DateFormat
import java.util.*

abstract class FileUtils {
    companion object {
        fun createFileUri(context: Context, fileType: String = Environment.DIRECTORY_PICTURES): Uri {
            val storageDir: File? = context.getExternalFilesDir(fileType)
            val file = File.createTempFile(
                createUniqueFilename(),
                null,
                storageDir
            )
            return FileProvider.getUriForFile(context, "com.example.polyglot.fileprovider", file)
        }

        fun createInternalFileUri(context: Context): Uri {
            return File.createTempFile(
                createUniqueFilename(),
                null,
                context.cacheDir
            ).toUri()
        }

        private fun createUniqueFilename(): String {
            return DateFormat.getDateTimeInstance().format(Date())
        }
    }
}