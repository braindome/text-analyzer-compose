package com.example.textanalyzer_compose

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextAnalyzerApi {

    @POST("word-list")
    suspend fun getWordList(@Body input: Input): Response<WordListResponse>

    @POST("word-count")
    suspend fun getWordCount(@Body input: Input): Response<WordCountResponse>

    @POST("summary")
    suspend fun getSummary(@Body input: Input): Response<SummaryResponse>

    @POST("stats")
    suspend fun getStats(@Body input: Input): Response<StatsResponse>
}