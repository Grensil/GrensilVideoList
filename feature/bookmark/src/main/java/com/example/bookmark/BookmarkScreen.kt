package com.example.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.designsystem.component.ImagePreviewDialog
import com.example.domain.model.Photo
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bookmark.component.BookmarkMediaGrid

@Composable
fun BookmarkScreen(
    navController: NavHostController,
    viewModel: BookmarkViewModel = hiltViewModel(),
    onVideoClick: (Long) -> Unit = {}
) {
    val uiVideos by viewModel.uiVideos.collectAsState()
    val uiPhotos by viewModel.uiPhotos.collectAsState()
    val videoBookmarkStates by viewModel.videoBookmarkStates.collectAsState()
    val photoBookmarkStates by viewModel.photoBookmarkStates.collectAsState()

    // 이미지 프리뷰 다이얼로그 상태
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                    onVideoClick = { video ->
                        viewModel.onVideoClicked(video)
                        onVideoClick(video.id)
                    },
                    onPhotoClick = { photo -> selectedPhoto = photo },
                    lazyGridState = allTabState
                )

                1 -> BookmarkMediaGrid(
                    videos = uiVideos,
                    photos = emptyList(),
                    videoBookmarkStates = videoBookmarkStates,
                    photoBookmarkStates = photoBookmarkStates,
                    onVideoBookmarkRemove = { video -> viewModel.removeVideoBookmark(video) },
                    onPhotoBookmarkRemove = { },
                    onVideoClick = { video ->
                        viewModel.onVideoClicked(video)
                        onVideoClick(video.id)
                    },
                    onPhotoClick = { },
                    lazyGridState = videoTabState
                )

                2 -> BookmarkMediaGrid(
                    videos = emptyList(),
                    photos = uiPhotos,
                    videoBookmarkStates = videoBookmarkStates,
                    photoBookmarkStates = photoBookmarkStates,
                    onVideoBookmarkRemove = { },
                    onPhotoBookmarkRemove = { photo -> viewModel.removePhotoBookmark(photo) },
                    onVideoClick = { },
                    onPhotoClick = { photo -> selectedPhoto = photo },
                    lazyGridState = photoTabState
                )
            }

            // 이미지 프리뷰 다이얼로그
            selectedPhoto?.let { photo ->
                ImagePreviewDialog(
                    imageUrl = photo.src.large2x ?: photo.src.large ?: photo.src.original,
                    contentDescription = photo.alt,
                    onDismiss = { selectedPhoto = null }
                )
            }
        }
    }
}