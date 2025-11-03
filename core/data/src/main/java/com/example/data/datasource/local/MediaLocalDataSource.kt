package com.example.data.datasource.local

import androidx.paging.PagingSource
import com.example.data.datasource.local.db.dao.PhotoDao
import com.example.data.datasource.local.db.dao.VideoDao
import com.example.data.datasource.local.db.entity.PhotoEntity
import com.example.data.datasource.local.db.entity.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaLocalDataSource @Inject constructor(
    private val videoDao: VideoDao,
    private val photoDao: PhotoDao
) {
    // Video operations
    fun getAllVideosPaged(): PagingSource<Int, VideoEntity> {
        return videoDao.getAllVideosPaged()
    }

    fun getAllVideos(): Flow<List<VideoEntity>> {
        return videoDao.getAllVideos()
    }

    fun getVideoById(id: Long): Flow<VideoEntity?> {
        return videoDao.getVideoById(id)
    }

    suspend fun getVideoByIdOnce(id: Long): VideoEntity? {
        return videoDao.getVideoByIdOnce(id)
    }

    suspend fun insertVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun insertVideos(videos: List<VideoEntity>) {
        videoDao.insertVideos(videos)
    }

    suspend fun updateVideo(video: VideoEntity) {
        videoDao.updateVideo(video)
    }

    suspend fun deleteVideo(video: VideoEntity) {
        videoDao.deleteVideo(video)
    }

    suspend fun deleteVideoById(id: Long) {
        videoDao.deleteVideoById(id)
    }

    suspend fun deleteAllVideos() {
        videoDao.deleteAllVideos()
    }

    suspend fun getVideoCount(): Int {
        return videoDao.getVideoCount()
    }

    // Photo operations
    fun getAllPhotosPaged(): PagingSource<Int, PhotoEntity> {
        return photoDao.getAllPhotosPaged()
    }

    fun getAllPhotos(): Flow<List<PhotoEntity>> {
        return photoDao.getAllPhotos()
    }

    fun getPhotoById(id: Long): Flow<PhotoEntity?> {
        return photoDao.getPhotoById(id)
    }

    suspend fun getPhotoByIdOnce(id: Long): PhotoEntity? {
        return photoDao.getPhotoByIdOnce(id)
    }

    suspend fun insertPhoto(photo: PhotoEntity) {
        photoDao.insertPhoto(photo)
    }

    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        photoDao.insertPhotos(photos)
    }

    suspend fun updatePhoto(photo: PhotoEntity) {
        photoDao.updatePhoto(photo)
    }

    suspend fun deletePhoto(photo: PhotoEntity) {
        photoDao.deletePhoto(photo)
    }

    suspend fun deletePhotoById(id: Long) {
        photoDao.deletePhotoById(id)
    }

    suspend fun deleteAllPhotos() {
        photoDao.deleteAllPhotos()
    }

    suspend fun getPhotoCount(): Int {
        return photoDao.getPhotoCount()
    }
}
