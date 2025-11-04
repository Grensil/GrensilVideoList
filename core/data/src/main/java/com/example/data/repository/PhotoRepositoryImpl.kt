package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.model.toDomain
import com.example.data.model.toEntity
import com.example.domain.model.Photo
import com.example.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val localDataSource: MediaLocalDataSource
) : PhotoRepository {



    // Local database operations - Paging (for large datasets)
    override fun getSavedPhotosPaged(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { localDataSource.getAllPhotosPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    // Local database operations - Simple (for small datasets like bookmarks)
    override fun getSavedPhotos(): Flow<List<Photo>> {
        return localDataSource.getAllPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSavedPhotoById(id: Long): Flow<Photo?> {
        return localDataSource.getPhotoById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun savePhoto(photo: Photo) {
        localDataSource.insertPhoto(photo.toEntity())
    }

    override suspend fun savePhotos(photos: List<Photo>) {
        localDataSource.insertPhotos(photos.map { it.toEntity() })
    }

    override suspend fun deletePhoto(photo: Photo) {
        localDataSource.deletePhoto(photo.toEntity())
    }

    override suspend fun deletePhotoById(id: Long) {
        localDataSource.deletePhotoById(id)
    }

    override suspend fun isPhotoSaved(id: Long): Boolean {
        return localDataSource.getPhotoByIdOnce(id) != null
    }
}
