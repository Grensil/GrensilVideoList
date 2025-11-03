package com.example.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUserDto(
    val id: Long,
    val name: String,
    val url: String
) : Parcelable