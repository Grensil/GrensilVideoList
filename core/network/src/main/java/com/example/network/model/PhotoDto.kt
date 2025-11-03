package com.example.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoDto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url")
    val photographerUrl: String,
    @SerializedName("photographer_id")
    val photographerId: Long,
    @SerializedName("avg_color")
    val avgColor: String,
    val src: PhotoSrcDto,
    val liked: Boolean,
    val alt: String
) : Parcelable

@Parcelize
data class PhotoSrcDto(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
) : Parcelable

@Parcelize
data class CuratedPhotosResponse(
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val photos: List<PhotoDto>,
    @SerializedName("next_page")
    val nextPage: String? = null
) : Parcelable
