package com.example.network.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoDto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: VideoUserDto,
    val videoFiles: List<VideoFileDto>
) : Parcelable