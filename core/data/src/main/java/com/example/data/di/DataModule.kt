package com.example.data.di

import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.repository.ApiKey
import com.example.data.repository.VideoRepositoryImpl
import com.example.domain.repository.VideoRepository
import com.example.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMediaRemoteDataSource(
        videoApi: VideoApi
    ): MediaRemoteDataSource {
        return MediaRemoteDataSource(videoApi)
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        remoteDataSource: MediaRemoteDataSource,
        @ApiKey apiKey: String
    ): VideoRepository {
        return VideoRepositoryImpl(remoteDataSource, apiKey)
    }
}