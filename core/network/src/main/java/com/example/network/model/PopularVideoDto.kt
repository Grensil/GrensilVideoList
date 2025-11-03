package com.example.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PopularVideoListResponse(
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    val url: String,
    val videos: List<VideoDto>,
    @SerializedName("next_page")
    val nextPage: String? = null
) : Parcelable