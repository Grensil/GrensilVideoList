package com.example.domain.model

data class VideoFile(
    val id: Long,
    val quality: String,
    val fileType: String,
    val width: Int,
    val height: Int,
    val link: String
)
