package com.example.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val mediaPagingItems = viewModel.mediaPagingData.collectAsLazyPagingItems()
    val bookmarkedVideos by viewModel.bookmarkedVideos.collectAsState()
    val bookmarkedPhotos by viewModel.bookmarkedPhotos.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MediaList(
                mediaPagingItems = mediaPagingItems,
                bookmarkedVideos = bookmarkedVideos,
                bookmarkedPhotos = bookmarkedPhotos,
                onVideoBookmarkClick = viewModel::toggleVideoBookmark,
                onPhotoBookmarkClick = viewModel::togglePhotoBookmark
            )
        }
    }
}