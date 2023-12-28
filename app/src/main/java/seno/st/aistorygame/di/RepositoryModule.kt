package seno.st.aistorygame.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import seno.st.aistorygame.data.AiRepository
import seno.st.aistorygame.data.AiRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAiRepository(aiRepositoryImpl: AiRepositoryImpl): AiRepository
}