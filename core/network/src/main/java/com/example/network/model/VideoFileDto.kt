package com.example.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFileDto(
    val id: Int,
    val quality: String,
    @SerializedName("file_type")
    val fileType: String,
    val width: Int,
    val height: Int,
    val link: String,
    val fps: Double? = null
) : Parcelable