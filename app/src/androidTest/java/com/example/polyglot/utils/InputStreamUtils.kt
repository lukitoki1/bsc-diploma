package com.example.polyglot.utils

import java.io.InputStream

abstract class InputStreamUtils {
    companion object {
        fun readLines(stream: InputStream): List<String> {
            return stream.bufferedReader().use { it.readText() }.lines()
        }
    }
}