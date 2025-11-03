package com.example.domain.model

data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val avgColor: String,
    val src: PhotoSrc,
    val alt: String
)

data class PhotoSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)
