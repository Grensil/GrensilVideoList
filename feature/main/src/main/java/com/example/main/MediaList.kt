package com.example.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.designsystem.theme.ShimmerBase
import com.example.designsystem.theme.ShimmerHighlight
import com.example.designsystem.theme.Teal
import com.example.domain.model.MediaItem
import com.example.domain.model.Photo
import com.example.domain.model.Video
import com.example.main.component.ErrorContent
import com.example.main.component.PhotoItem
import com.example.main.component.VideoItem


@Composable
fun MediaList(
    mediaPagingItems: LazyPagingItems<MediaItem>,
    bookmarkedVideos: Map<Long, Boolean>,
    bookmarkedPhotos: Map<Long, Boolean>,
    currentPlayingVideoId: Long?,
    playbackProgress: Float,
    remainingSeconds: Int,
    exoPlayer: ExoPlayer?,
    listState: LazyListState = rememberLazyListState(),
    onVideoBookmarkClick: (Video) -> Unit,
    onPhotoBookmarkClick: (Photo) -> Unit,
    onVideoClick: (Video) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        items(mediaPagingItems.itemCount) { index ->
            mediaPagingItems[index]?.let { mediaItem ->
                when (mediaItem) {
                    is MediaItem.VideoItem -> VideoItem(
                        video = mediaItem.video,
                        isBookmarked = bookmarkedVideos[mediaItem.video.id] == true,
                        isPreviewPlaying = mediaItem.video.id == currentPlayingVideoId,
                        playbackProgress = if (mediaItem.video.id == currentPlayingVideoId) playbackProgress else 0f,
                        remainingSeconds = if (mediaItem.video.id == currentPlayingVideoId) remainingSeconds else 0,
                        exoPlayer = if (mediaItem.video.id == currentPlayingVideoId) exoPlayer else null,
                        onBookmarkClick = onVideoBookmarkClick,
                        onVideoClick = onVideoClick
                    )
                    is MediaItem.PhotoItem -> PhotoItem(
                        photo = mediaItem.photo,
                        isBookmarked = bookmarkedPhotos[mediaItem.photo.id] == true,
                        onBookmarkClick = onPhotoBookmarkClick
                    )
                }
            }
        }

        // Loading states
        mediaPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    items(3) {
                        ShimmerMediaItem()
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Teal,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val error = (loadState.refresh as LoadState.Error).error
                    item {
                        ErrorContent(
                            message = error.message ?: "Unknown error occurred",
                            onRetryClick = { retry() }
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    val error = (loadState.append as LoadState.Error).error
                    item {
                        ErrorContent(
                            message = error.message ?: "Failed to load more",
                            onRetryClick = { retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerMediaItem() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            ShimmerBase,
            ShimmerHighlight,
            ShimmerBase
        ),
        start = Offset(shimmerTranslate - 200f, shimmerTranslate - 200f),
        end = Offset(shimmerTranslate, shimmerTranslate)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(shimmerBrush)
    ) {
        // Badge placeholder
        Box(
            modifier = Modifier
                .padding(12.dp)
                .width(50.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ShimmerBase)
                .align(Alignment.TopStart)
        )

        // Center circle placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(ShimmerBase)
                .align(Alignment.Center)
        )

        // Bottom info placeholder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBase)
            )
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShimmerBase)
            )
        }
    }
}