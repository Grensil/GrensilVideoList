package com.example.network.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
    @SerializedName("video_files")
    val videoFiles: List<VideoFileDto>,
    @SerializedName("video_pictures")
    val videoPictures: List<VideoPictureDto>? = null
) : Parcelable