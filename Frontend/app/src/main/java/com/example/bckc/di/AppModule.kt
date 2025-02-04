package com.example.bckc.di

import android.content.Context
import com.example.bckc.data.api.ApiService
import com.example.bckc.utils.PreferenceManager
import com.example.bckc.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferenceManager(
        @ApplicationContext context: Context
    ): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        apiService: ApiService,
        preferenceManager: PreferenceManager
    ): TokenManager {
        return TokenManager(apiService, preferenceManager)
    }
}
