package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    // Local database operations - Paging (for large datasets)
    fun getSavedPhotosPaged(): Flow<PagingData<Photo>>

    // Local database operations - Simple (for small datasets like bookmarks)
    fun getSavedPhotos(): Flow<List<Photo>>
    fun getSavedPhotoById(id: Long): Flow<Photo?>
    suspend fun savePhoto(photo: Photo)
    suspend fun savePhotos(photos: List<Photo>)
    suspend fun deletePhoto(photo: Photo)
    suspend fun deletePhotoById(id: Long)
    suspend fun isPhotoSaved(id: Long): Boolean
}
