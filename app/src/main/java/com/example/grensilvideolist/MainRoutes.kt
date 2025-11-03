package com.example.grensilvideolist

sealed class Routes(val path: String) {

    data object Home : Routes("home")
    data object Bookmark : Routes("bookmark")
}