package com.example.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFileDto(
    val id: Int,
    val qualify: String,
    val file_type: String,
    val width: Int,
    val height: Int,
    val link: String
) : Parcelable