package com.example.network.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoDto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: VideoUserDto,
    val video_files: List<VideoFileDto>,
    val video_pictures: List<VideoPictureDto>? = null
) : Parcelable