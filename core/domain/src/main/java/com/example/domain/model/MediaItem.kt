package com.example.domain.model

sealed class MediaItem {
    data class VideoItem(val video: Video) : MediaItem()
    data class PhotoItem(val photo: Photo) : MediaItem()
}
