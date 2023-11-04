package com.example.textanalyzer_compose

data class SummaryResponse(
    val wordCount: Int,
    val wordList: List<String>
)