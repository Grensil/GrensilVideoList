package com.example.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PopularVideoListResponse(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: List<VideoDto>
) : Parcelable