package com.example.textanalyzer_compose

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextAnalyzerApi {

    @POST("word-list")
    suspend fun  getWordList(@Body input: Input): Response<WordListResponse>
}