package seno.st.aistorygame.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import seno.st.aistorygame.data.DefaultParamsInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TIMEOUT_SECONDS = 45L
private const val BASE_URL = "https://api.openai.com"

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideDefaultParamsInterceptor(): DefaultParamsInterceptor = DefaultParamsInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        defaultParamsInterceptor: DefaultParamsInterceptor,
    ) = OkHttpClient.Builder()
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addNetworkInterceptor(defaultParamsInterceptor)
        .addNetworkInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        ).build()

    @Singleton
    @Provides
    fun provideRetrofit(@NetworkGson gson: Gson, okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

}