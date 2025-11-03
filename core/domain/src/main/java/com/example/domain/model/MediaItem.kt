package com.example.domain.model

sealed class MediaItem {
    data class VideoItem(val video: Video, val isBookmarked: Boolean = false) : MediaItem()
    data class PhotoItem(val photo: Photo, val isBookmarked: Boolean = false) : MediaItem()
}
