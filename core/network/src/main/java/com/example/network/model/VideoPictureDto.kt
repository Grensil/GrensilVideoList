package com.example.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoPictureDto(
    val id: Long,
    val nr: Int,
    val picture: String
) : Parcelable
