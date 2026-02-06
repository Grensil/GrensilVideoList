package com.example.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.example.designsystem.component.ImagePreviewDialog
import com.example.domain.model.Photo
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.domain.model.MediaItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


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
    val isVideoActuallyPlaying by viewModel.isVideoActuallyPlaying.collectAsState()

    // 이미지 프리뷰 다이얼로그 상태
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

    val listState = rememberLazyListState()
    val exoPlayer = viewModel.videoPlayerManager.getPlayer()

    // 완전히 보이는 첫 번째 비디오 찾기 함수
    fun findFirstFullyVisibleVideo(): com.example.domain.model.Video? {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo
        if (visibleItems.isEmpty()) return null

        val viewportStart = layoutInfo.viewportStartOffset
        val viewportEnd = layoutInfo.viewportEndOffset

        // 완전히 보이는 아이템 중 첫 번째 비디오 찾기
        for (item in visibleItems) {
            val itemStart = item.offset
            val itemEnd = item.offset + item.size

            // 아이템이 완전히 보이는지 확인
            val isFullyVisible = itemStart >= viewportStart && itemEnd <= viewportEnd

            if (isFullyVisible && item.index < mediaPagingItems.itemCount) {
                val mediaItem = mediaPagingItems[item.index]
                if (mediaItem is MediaItem.VideoItem) {
                    return mediaItem.video
                }
            }
        }

        // 완전히 보이는 비디오가 없으면 가장 많이 보이는 비디오 찾기
        return visibleItems
            .filter { item ->
                val mediaItem = if (item.index < mediaPagingItems.itemCount) {
                    mediaPagingItems[item.index]
                } else null
                mediaItem is MediaItem.VideoItem
            }
            .maxByOrNull { item ->
                val itemStart = item.offset.coerceAtLeast(viewportStart)
                val itemEnd = (item.offset + item.size).coerceAtMost(viewportEnd)
                itemEnd - itemStart // 보이는 영역 크기
            }?.let { item ->
                val mediaItem = mediaPagingItems[item.index]
                (mediaItem as? MediaItem.VideoItem)?.video
            }
    }

    // 초기 로드 시 첫 번째 비디오 자동 재생
    LaunchedEffect(mediaPagingItems.itemCount) {
        if (mediaPagingItems.itemCount > 0 && currentPlayingVideoId == null) {
            kotlinx.coroutines.delay(500) // 레이아웃 안정화 대기
            findFirstFullyVisibleVideo()?.let { video ->
                viewModel.onCenterVideoChanged(video)
            }
        }
    }

    // 스크롤 멈춤 감지 및 완전히 보이는 첫 번째 비디오 찾기
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .debounce(300) // 스크롤이 멈춘 후 300ms 대기
            .collect { isScrolling ->
                if (!isScrolling) {
                    findFirstFullyVisibleVideo()?.let { video ->
                        viewModel.onCenterVideoChanged(video)
                    } ?: viewModel.onCenterVideoChanged(null)
                }
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
                isVideoActuallyPlaying = isVideoActuallyPlaying,
                playbackProgress = playbackProgress,
                remainingSeconds = remainingSeconds,
                exoPlayer = exoPlayer,
                listState = listState,
                onVideoBookmarkClick = viewModel::toggleVideoBookmark,
                onPhotoBookmarkClick = viewModel::togglePhotoBookmark,
                onVideoClick = { video ->
                    viewModel.onVideoClicked(video)
                    onVideoClick(video.id)
                },
                onPhotoClick = { photo ->
                    selectedPhoto = photo
                }
            )

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