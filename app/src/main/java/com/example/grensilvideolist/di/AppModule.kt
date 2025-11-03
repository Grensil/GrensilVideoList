package com.example.grensilvideolist.di

import com.example.data.repository.ApiKey
import com.example.grensilvideolist.BuildConfig
import com.example.network.di.IsDebugMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @ApiKey
    fun provideApiKey(): String {
        return BuildConfig.API_KEY
    }

    @Provides
    @Singleton
    @IsDebugMode
    fun provideIsDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }
}
