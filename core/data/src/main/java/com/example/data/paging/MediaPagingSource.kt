package com.example.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.model.toDomain
import com.example.domain.model.MediaItem

class MediaPagingSource(
    private val remoteDataSource: MediaRemoteDataSource,
    private val localDataSource: MediaLocalDataSource,
    private val apiKey: String
) : PagingSource<Int, MediaItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            // 비디오와 이미지를 각각 절반씩 가져오기
            val videoPerPage = pageSize / 2
            val photoPerPage = pageSize - videoPerPage

            val videos = remoteDataSource.getPopularVideos(
                apiKey = apiKey,
                page = page,
                perPage = videoPerPage
            ).videos.map {
                val video = it.toDomain()
                val isBookmarked = localDataSource.getVideoByIdOnce(video.id) != null
                MediaItem.VideoItem(video, isBookmarked)
            }

            val photos = remoteDataSource.getCuratedPhotos(
                apiKey = apiKey,
                page = page,
                perPage = photoPerPage
            ).photos.map {
                val photo = it.toDomain()
                val isBookmarked = localDataSource.getPhotoByIdOnce(photo.id) != null
                MediaItem.PhotoItem(photo, isBookmarked)
            }

            // 비디오와 이미지를 번갈아가면서 섞기
            val combined = mutableListOf<MediaItem>()
            val maxSize = maxOf(videos.size, photos.size)
            for (i in 0 until maxSize) {
                if (i < videos.size) combined.add(videos[i])
                if (i < photos.size) combined.add(photos[i])
            }

            LoadResult.Page(
                data = combined.shuffled(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (combined.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
