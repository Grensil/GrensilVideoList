package com.example.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.domain.model.MediaItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.math.abs


@OptIn(FlowPreview::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    onVideoClick: (Long) -> Unit = {}
) {
    val mediaPagingItems = viewModel.mediaPagingData.collectAsLazyPagingItems()
    val bookmarkedVideos by viewModel.bookmarkedVideos.collectAsState()
    val bookmarkedPhotos by viewModel.bookmarkedPhotos.collectAsState()
    val currentPlayingVideoId by viewModel.currentPlayingVideoId.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    val listState = rememberLazyListState()
    val exoPlayer = viewModel.videoPlayerManager.getPlayer()

    // 스크롤 멈춤 감지 및 가장 중앙에 가까운 비디오 찾기
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .debounce(300) // 스크롤이 멈춘 후 300ms 대기
            .collect { isScrolling ->
                if (!isScrolling) {
                    // 스크롤이 멈추면 가장 중앙에 가까운 비디오 찾기
                    val layoutInfo = listState.layoutInfo
                    val visibleItems = layoutInfo.visibleItemsInfo
                    if (visibleItems.isEmpty()) return@collect

                    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

                    // 모든 visible 아이템 중에서 비디오만 필터링하고, 중앙에 가장 가까운 것 찾기
                    val closestVideoIndex = visibleItems
                        .filter { item ->
                            val mediaItem = if (item.index < mediaPagingItems.itemCount) {
                                mediaPagingItems[item.index]
                            } else null
                            mediaItem is MediaItem.VideoItem
                        }
                        .minByOrNull { item ->
                            abs((item.offset + item.size / 2) - viewportCenter)
                        }?.index

                    if (closestVideoIndex != null && closestVideoIndex < mediaPagingItems.itemCount) {
                        val mediaItem = mediaPagingItems[closestVideoIndex]
                        if (mediaItem is MediaItem.VideoItem) {
                            viewModel.onCenterVideoChanged(mediaItem.video)
                        }
                    } else {
                        viewModel.onCenterVideoChanged(null)
                    }
                }
            }
    }

    // 라이프사이클 관리
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.videoPlayerManager.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (currentPlayingVideoId != null) {
                        viewModel.videoPlayerManager.play()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            MediaList(
                mediaPagingItems = mediaPagingItems,
                bookmarkedVideos = bookmarkedVideos,
                bookmarkedPhotos = bookmarkedPhotos,
                currentPlayingVideoId = currentPlayingVideoId,
                playbackProgress = playbackProgress,
                remainingSeconds = remainingSeconds,
                exoPlayer = exoPlayer,
                listState = listState,
                onVideoBookmarkClick = viewModel::toggleVideoBookmark,
                onPhotoBookmarkClick = viewModel::togglePhotoBookmark,
                onVideoClick = { video ->
                    viewModel.onVideoClicked(video)
                    onVideoClick(video.id)
                }
            )
        }
    }
}