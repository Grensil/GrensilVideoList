package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.BuildConfig
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.datasource.local.db.GrensilVideoListDatabase
import com.example.data.datasource.local.db.dao.PhotoDao
import com.example.data.datasource.local.db.dao.VideoDao
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.repository.ApiKey
import com.example.data.repository.MediaRepositoryImpl
import com.example.data.repository.PhotoRepositoryImpl
import com.example.data.repository.VideoRepositoryImpl
import com.example.data.security.DatabaseEncryptionHelper
import com.example.domain.repository.MediaRepository
import com.example.domain.repository.PhotoRepository
import com.example.domain.repository.VideoRepository
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
        val builder = Room.databaseBuilder(
            context,
            GrensilVideoListDatabase::class.java,
            GrensilVideoListDatabase.DATABASE_NAME
        )

        // Release 빌드에서만 DB 암호화 적용
        // 개발 중에는 암호화하지 않아 디버깅이 쉬움
        if (!BuildConfig.DEBUG) {
            try {
                val encryptionHelper = DatabaseEncryptionHelper(context)
                builder.openHelperFactory(encryptionHelper.getEncryptedFactory())
            } catch (e: Exception) {
                // 암호화 실패 시 로그만 남기고 암호화 없이 진행
                // 실제 배포시에는 앱을 종료해야 할 수도 있음
                android.util.Log.e("DataModule", "DB encryption failed", e)
            }
        }

        return builder.build()
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
        localDataSource: MediaLocalDataSource
    ): VideoRepository {
        return VideoRepositoryImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(
        localDataSource: MediaLocalDataSource
    ): PhotoRepository {
        return PhotoRepositoryImpl(localDataSource)
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