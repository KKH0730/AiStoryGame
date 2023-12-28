package seno.st.aistorygame.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import seno.st.aistorygame.data.AiService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {

    @Singleton
    @Provides
    fun provideAiService(retrofit: Retrofit): AiService = retrofit.create()
}