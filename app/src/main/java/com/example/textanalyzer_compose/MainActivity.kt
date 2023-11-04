package com.example.textanalyzer_compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.textanalyzer_compose.ui.theme.TextAnalyzercomposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            TextAnalyzercomposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {

    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("https://0ldr1q08xd.execute-api.eu-north-1.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val scope = rememberCoroutineScope()

    val api = retrofit.create(TextAnalyzerApi::class.java)
    val inputState = remember { mutableStateOf(TextFieldValue(""))}
    val outputState = remember { mutableStateOf("")}
    val wordCountState = remember { mutableStateOf(0)}


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            OutlinedTextField(
                value = inputState.value,
                onValueChange = { inputState.value = it  },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Handle done action here
                    }
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            getWordList(
                                api,
                                inputState.value.text,
                                outputState
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("word list")
                }
                Button(
                    onClick = {
                          scope.launch {
                              getSummary(
                                  api,
                                  inputState.value.text,
                                  outputState,
                                  wordCountState
                              )

                          }
                    },

                    modifier = Modifier.weight(1f)
                ) {
                    Text("summary")
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            getWordCount(
                                api,
                                inputState.value.text,
                                outputState,
                                wordCountState)
                    } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("count")
                }
                Button(
                    onClick = {
                        scope.launch {
                            getStats(
                                api,
                                inputState.value.text,
                                outputState
                            )

                    } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("stats")
                }
            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
        ) {
            Text(
                text = outputState.value,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

suspend fun getWordList(api: TextAnalyzerApi, inputStateText: String, outputState: MutableState<String>) {
    try {
        val response = withContext(Dispatchers.IO) {
            api.getWordList(Input(inputStateText))
        }

        if (response.isSuccessful) {
            val wordListResponse = response.body()
            if (wordListResponse != null) {
                withContext(Dispatchers.Main) {
                    outputState.value = wordListResponse.wordList.joinToString(", ")
                }
            } else {
                // Handle null response
                Log.d("xxx", "Null response body")
            }
        } else {
            // Handle non-successful response (e.g., 404, 500, etc.)
            Log.d("xxx", "Non-successful response: ${response.code()}")
        }
    } catch (e: Exception) {
        // Handle potential exceptions
        Log.d("xxx", "Error: ${e.message}")
    }
}

suspend fun getStats(api: TextAnalyzerApi, inputStateText: String, outputState: MutableState<String>) {
    try {
        val response = withContext(Dispatchers.IO) {
            api.getStats(Input(inputStateText))
        }

        if (response.isSuccessful) {
            val statsResponse = response.body()
            if (statsResponse != null) {
                withContext(Dispatchers.Main) {
                    outputState.value = statsResponse.stats.joinToString(", ")
                }
            } else {
                // Handle null response
                Log.d("xxx", "Null response body")
            }
        } else {
            // Handle non-successful response (e.g., 404, 500, etc.)
            Log.d("xxx", "Non-successful response: ${response.code()}")
        }
    } catch (e: Exception) {
        // Handle potential exceptions
        Log.d("xxx", "Error: ${e.message}")
    }
}

suspend fun getWordCount(api: TextAnalyzerApi, inputStateText: String, outputState: MutableState<String>, wordCountState: MutableState<Int>) {
    try {
        val response = withContext(Dispatchers.IO) {
            api.getWordCount(Input(inputStateText))
        }

        if (response.isSuccessful) {
            val wordCountResponse = response.body()
            if (wordCountResponse != null) {
                withContext(Dispatchers.Main) {
                    wordCountState.value = wordCountResponse.wordCount
                    outputState.value = wordCountState.value.toString()
                }
            } else {
                // Handle null response
                Log.d("xxx", "Null response body")
            }
        } else {
            // Handle non-successful response (e.g., 404, 500, etc.)
            Log.d("xxx", "Non-successful response: ${response.code()}")
        }
    } catch (e: Exception) {
        // Handle potential exceptions
        Log.d("xxx", "Error: ${e.message}")
    }
}

suspend fun getSummary(api: TextAnalyzerApi, inputStateText: String, outputState: MutableState<String>, wordCountState: MutableState<Int>) {
    try {
        val response = withContext(Dispatchers.IO) {
            api.getSummary(Input(inputStateText))
        }

        if (response.isSuccessful) {
            val summaryResponse = response.body()
            if (summaryResponse != null) {
                withContext(Dispatchers.Main) {
                    wordCountState.value = summaryResponse.wordCount
                    val wordListString = summaryResponse.wordList.joinToString(", ")
                    outputState.value = "Word Count: " + wordCountState.value.toString() + "Word List: " + wordListString
                }
            } else {
                // Handle null response
                Log.d("xxx", "Null response body")
            }
        } else {
            // Handle non-successful response (e.g., 404, 500, etc.)
            Log.d("xxx", "Non-successful response: ${response.code()}")
        }
    } catch (e: Exception) {
        // Handle potential exceptions
        Log.d("xxx", "Error: ${e.message}")
    }
}

@Composable
fun App() {
    AppContent()
}

@Preview
@Composable
fun AppPreview() {
    App()
}


@Preview(showBackground = true)
@Composable
fun AppContentPreview() {
    AppContent()
}

