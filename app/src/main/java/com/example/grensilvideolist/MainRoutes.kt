package com.example.grensilvideolist

sealed class Routes(val path: String) {

    data object Home : Routes("home")
    data object Bookmark : Routes("bookmark")
    data object VideoDetail : Routes("videoDetail/{videoId}") {
        fun createRoute(videoId: Long) = "videoDetail/$videoId"
    }
}