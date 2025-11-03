package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.datasource.local.db.GrensilVideoListDatabase
import com.example.data.datasource.local.db.dao.PhotoDao
import com.example.data.datasource.local.db.dao.VideoDao
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.repository.ApiKey
import com.example.data.repository.MediaRepositoryImpl
import com.example.data.repository.PhotoRepositoryImpl
import com.example.data.repository.VideoRepositoryImpl
import com.example.domain.repository.remote.MediaRepository
import com.example.domain.repository.local.PhotoRepository
import com.example.domain.repository.local.VideoRepository
import com.example.network.api.ImageApi
import com.example.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGrensilVideoListDatabase(
        @ApplicationContext context: Context
    ): GrensilVideoListDatabase {
        return Room.databaseBuilder(
            context,
            GrensilVideoListDatabase::class.java,
            GrensilVideoListDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideVideoDao(database: GrensilVideoListDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    fun providePhotoDao(database: GrensilVideoListDatabase): PhotoDao {
        return database.photoDao()
    }

    @Provides
    @Singleton
    fun provideMediaLocalDataSource(
        videoDao: VideoDao,
        photoDao: PhotoDao
    ): MediaLocalDataSource {
        return MediaLocalDataSource(videoDao, photoDao)
    }

    @Provides
    @Singleton
    fun provideMediaRemoteDataSource(
        videoApi: VideoApi,
        imageApi: ImageApi
    ): MediaRemoteDataSource {
        return MediaRemoteDataSource(videoApi, imageApi)
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        remoteDataSource: MediaRemoteDataSource,
        localDataSource: MediaLocalDataSource,
        @ApiKey apiKey: String
    ): VideoRepository {
        return VideoRepositoryImpl(remoteDataSource, localDataSource, apiKey)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(

        localDataSource: MediaLocalDataSource,
        @ApiKey apiKey: String
    ): PhotoRepository {
        return PhotoRepositoryImpl(localDataSource, apiKey)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(
        remoteDataSource: MediaRemoteDataSource,
        localDataSource: MediaLocalDataSource,
        @ApiKey apiKey: String
    ): MediaRepository {
        return MediaRepositoryImpl(remoteDataSource, localDataSource, apiKey)
    }
}