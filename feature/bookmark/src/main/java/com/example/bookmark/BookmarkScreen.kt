package com.example.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bookmark.component.BookmarkMediaGrid

@Composable
fun BookmarkScreen(
    navController: NavHostController,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val uiVideos by viewModel.uiVideos.collectAsState()
    val uiPhotos by viewModel.uiPhotos.collectAsState()
    val videoBookmarkStates by viewModel.videoBookmarkStates.collectAsState()
    val photoBookmarkStates by viewModel.photoBookmarkStates.collectAsState()

    // 화면 재진입 시 실제 북마크된 항목만 로드
    DisposableEffect(Unit) {
        viewModel.loadBookmarks()
        onDispose { }
    }

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("ALL", "VIDEO", "PHOTO")

    // 각 탭의 스크롤 위치를 독립적으로 저장
    val allTabState = rememberLazyGridState()
    val videoTabState = rememberLazyGridState()
    val photoTabState = rememberLazyGridState()

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> BookmarkMediaGrid(
                    videos = uiVideos,
                    photos = uiPhotos,
                    videoBookmarkStates = videoBookmarkStates,
                    photoBookmarkStates = photoBookmarkStates,
                    onVideoBookmarkRemove = { viewModel.removeVideoBookmark(it) },
                    onPhotoBookmarkRemove = { viewModel.removePhotoBookmark(it) },
                    lazyGridState = allTabState
                )

                1 -> BookmarkMediaGrid(
                    videos = uiVideos,
                    photos = emptyList(),
                    videoBookmarkStates = videoBookmarkStates,
                    photoBookmarkStates = photoBookmarkStates,
                    onVideoBookmarkRemove = { video -> viewModel.removeVideoBookmark(video) },
                    onPhotoBookmarkRemove = { },
                    lazyGridState = videoTabState
                )

                2 -> BookmarkMediaGrid(
                    videos = emptyList(),
                    photos = uiPhotos,
                    videoBookmarkStates = videoBookmarkStates,
                    photoBookmarkStates = photoBookmarkStates,
                    onVideoBookmarkRemove = { },
                    onPhotoBookmarkRemove = { photo -> viewModel.removePhotoBookmark(photo) },
                    lazyGridState = photoTabState
                )
            }
        }
    }
}