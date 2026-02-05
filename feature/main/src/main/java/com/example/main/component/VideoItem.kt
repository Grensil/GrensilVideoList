package com.example.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.designsystem.theme.BookmarkActive
import com.example.designsystem.theme.DarkCard
import com.example.designsystem.theme.GradientEnd
import com.example.designsystem.theme.GradientStart
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.designsystem.theme.VideoBadge
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser


@Composable
fun VideoItem(
    video: Video,
    isBookmarked: Boolean = false,
    isPreviewPlaying: Boolean = false,
    playbackProgress: Float = 0f,
    remainingSeconds: Int = 0,
    exoPlayer: ExoPlayer? = null,
    onBookmarkClick: (Video) -> Unit = {},
    onVideoClick: (Video) -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bookmark_scale"
    )

    val bookmarkColor by animateColorAsState(
        targetValue = if (isBookmarked) BookmarkActive else Color.White.copy(alpha = 0.8f),
        label = "bookmark_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f)
            )
            .clickable { onVideoClick(video) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 썸네일 (항상 배경에 유지 - 고화질 이미지 사용)
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                model = video.image,
                contentScale = ContentScale.Crop,
                contentDescription = "video thumbnail"
            )

            // 비디오 프리뷰 (재생 중일 때 위에 오버레이)
            if (isPreviewPlaying && exoPlayer != null) {
                VideoPreviewSurface(
                    exoPlayer = exoPlayer,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd),
                            startY = 100f
                        )
                    )
            )

            // Video badge (Top Left)
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VideoBadge.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "VIDEO",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Play button (Center) - 프리뷰 재생 중이 아닐 때만 표시
            if (!isPreviewPlaying) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play video",
                        tint = VideoBadge,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Bookmark button (Top Right)
            IconButton(
                onClick = { onBookmarkClick(video) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = bookmarkColor,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(scale)
                )
            }

            // Bottom section with progress bar and info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // Progress bar - 프리뷰 재생 중일 때 표시
                if (isPreviewPlaying) {
                    VideoProgressBar(
                        progress = playbackProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )
                }

                // Bottom info (위쪽에 표시)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = if (isPreviewPlaying) 8.dp else 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.user.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Duration/Remaining time chip (통일된 표시)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isPreviewPlaying && remainingSeconds > 0) {
                                formatRemainingTime(remainingSeconds)
                            } else {
                                formatDuration(video.duration)
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        "${minutes}:${secs.toString().padStart(2, '0')}"
    } else {
        "0:${secs.toString().padStart(2, '0')}"
    }
}

private fun formatRemainingTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        "${minutes}:${secs.toString().padStart(2, '0')}"
    } else {
        "0:${secs.toString().padStart(2, '0')}"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "VideoItem - Not Bookmarked")
@Composable
fun PreviewVideoItem() {
    GrensilVideoListTheme {
        VideoItem(
            video = Video(
                id = 1,
                width = 400,
                height = 200,
                url = "url",
                image = "image",
                duration = 55,
                user = VideoUser(
                    id = 222,
                    name = "John Creator",
                    url = "url"
                ),
                videoFiles = listOf(
                    VideoFile(
                        id = 344,
                        quality = "HD",
                        fileType = "fileType",
                        width = 400,
                        height = 200,
                        link = "link"
                    )
                ),
                videoPictures = listOf(
                    VideoPicture(
                        id = 1,
                        picture = "https://images.pexels.com/videos/3571264/free-video-3571264.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                        nr = 0
                    )
                )
            ),
            isBookmarked = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "VideoItem - Bookmarked")
@Composable
fun PreviewVideoItemBookmarked() {
    GrensilVideoListTheme {
        VideoItem(
            video = Video(
                id = 2,
                width = 400,
                height = 200,
                url = "url",
                image = "image",
                duration = 185,
                user = VideoUser(
                    id = 223,
                    name = "Jane Creator with a Very Long Name",
                    url = "url"
                ),
                videoFiles = listOf(
                    VideoFile(
                        id = 345,
                        quality = "4K",
                        fileType = "fileType",
                        width = 400,
                        height = 200,
                        link = "link"
                    )
                ),
                videoPictures = listOf(
                    VideoPicture(
                        id = 2,
                        picture = "https://images.pexels.com/videos/3571264/free-video-3571264.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                        nr = 0
                    )
                )
            ),
            isBookmarked = true
        )
    }
}
