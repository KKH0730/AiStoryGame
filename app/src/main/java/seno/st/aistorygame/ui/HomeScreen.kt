package seno.st.aistorygame.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import seno.st.aistorygame.extension.textDp
import seno.st.aistorygame.ui.story_game.StoryGameActivity
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "move",
            fontSize = 16.textDp,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 200.dp)
                .background(color = Color.Blue)
                .clickable {
                    context.startActivity(Intent(context, StoryGameActivity::class.java))
//                    scope.launch {
//                        AiRequest()
//                            .send(
//                                createRetrofit(
//                                    baseUrl = "https://api.openai.com"
//                                ).create(AiService::class.java),
//                                mapOf(
//                                    "model" to "gpt-3.5-turbo",
//                                    "messages" to listOf(
//                                        mapOf("role" to "system", "content" to "You are a helpful assistant."),
//                                        mapOf("role" to "user", "content" to "Hello!."),
//                                    )
//                                )
//                            )
//                            .collectLatest {
//                                Timber.e("kkhdev it : $it")
//                            }
//                    }
                }
        )
    }
}

private val httpClient: OkHttpClient
    get() = createClient()

private val httpLoggingInterceptor: HttpLoggingInterceptor
    get() = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

fun createClient(): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .connectTimeout(45, TimeUnit.SECONDS)
        .addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()

            // Default Header + Added Header
            val defaultHeaders = createDefaultHeaders()
            val existingHeaders = request.headers
            addHeaderIfAbsent(requestBuilder, existingHeaders, defaultHeaders)
            chain.proceed(requestBuilder.build())
        }
        .apply {
            addInterceptor(httpLoggingInterceptor)
//            addInterceptor(Interceptor { chain ->
//                val request: Request = chain.request()
//                val response: Response = chain.proceed(request)
//                response
//            })
        }
        .build()
}

private fun createDefaultHeaders(): MutableMap<String, String?> {
    return hashMapOf(
        "Content-Type" to "application/json"
    )
}

private fun addHeaderIfAbsent(
    requestBuilder: Request.Builder,
    existingHeaders: Headers,
    headers: Map<String, String?>,
) {
    for ((name, value) in headers) {
        if (existingHeaders.notContains(name) && value != null) {
            requestBuilder.addHeader(name, value)
        }
    }
}

private fun Headers.notContains(name: String): Boolean = values(name).isEmpty()

fun createRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(httpClient)
        .build()
}