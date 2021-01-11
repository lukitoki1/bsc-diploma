package com.example.polyglot.utils

import info.debatty.java.stringsimilarity.Cosine

abstract class StatUtils {
    companion object {
        fun countWords(text: String): MutableMap<String, Int> {
            val wordMap = mutableMapOf<String, Int>().withDefault { 0 }
            val splitText = text.split(" ")
            for (word in splitText) {
                wordMap[word] = wordMap.getValue(word) + 1
            }
            return wordMap
        }

        fun calculateCosine(cosine: Cosine, profile1: MutableMap<String, Int>, profile2:MutableMap<String, Int>): Double {
            return cosine.similarity(profile1, profile2).let {
                if (it > 1.0) {
                    1.0
                } else it
            }
        }
    }
}