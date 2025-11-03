package com.example.domain.model

data class Video(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: VideoUser,
    val videoFiles: List<VideoFile>
)
