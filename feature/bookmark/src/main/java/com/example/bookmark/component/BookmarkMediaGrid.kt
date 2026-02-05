package com.example.bookmark.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.designsystem.theme.BookmarkActive
import com.example.designsystem.theme.DarkCard
import com.example.designsystem.theme.GradientEnd
import com.example.designsystem.theme.GradientStart
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.designsystem.theme.PhotoBadge
import com.example.designsystem.theme.VideoBadge
import com.example.designsystem.util.TimeFormatUtils
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser

// 북마크 상태에 따른 투명도 상수
private const val BOOKMARKED_ALPHA = 1f
private const val UNBOOKMARKED_ALPHA = 0.5f

// 공통 애니메이션 스펙
private val bookmarkAnimationSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

@Composable
fun BookmarkMediaGrid(
    videos: List<Video>,
    photos: List<Photo>,
    videoBookmarkStates: Map<Long, Boolean>,
    photoBookmarkStates: Map<Long, Boolean>,
    onVideoBookmarkRemove: (Video) -> Unit,
    onPhotoBookmarkRemove: (Photo) -> Unit,
    onVideoClick: (Video) -> Unit = {},
    onPhotoClick: (Photo) -> Unit = {},
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        // Videos
        items(
            items = videos,
            key = { video -> "video_${video.id}" }
        ) { video ->
            val isBookmarked = videoBookmarkStates[video.id] ?: true
            BookmarkVideoGridItem(
                video = video,
                isBookmarked = isBookmarked,
                onBookmarkRemove = { onVideoBookmarkRemove(video) },
                onClick = { onVideoClick(video) }
            )
        }

        // Photos
        items(
            items = photos,
            key = { photo -> "photo_${photo.id}" }
        ) { photo ->
            val isBookmarked = photoBookmarkStates[photo.id] ?: true
            BookmarkPhotoGridItem(
                photo = photo,
                isBookmarked = isBookmarked,
                onBookmarkRemove = { onPhotoBookmarkRemove(photo) },
                onClick = { onPhotoClick(photo) }
            )
        }
    }
}

@Composable
fun BookmarkVideoGridItem(
    video: Video,
    isBookmarked: Boolean,
    onBookmarkRemove: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.2f else 1f,
        animationSpec = bookmarkAnimationSpec,
        label = "bookmark_scale"
    )

    val bookmarkColor by animateColorAsState(
        targetValue = if (isBookmarked) BookmarkActive else Color.White.copy(alpha = 0.8f),
        label = "bookmark_color"
    )

    // 북마크 해제 시 투명도 효과 (아이템이 곧 사라질 것임을 시각적으로 표시)
    val cardAlpha by animateFloatAsState(
        targetValue = if (isBookmarked) BOOKMARKED_ALPHA else UNBOOKMARKED_ALPHA,
        animationSpec = bookmarkAnimationSpec,
        label = "card_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer { alpha = cardAlpha }
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video thumbnail
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                model = video.videoPictures?.getOrNull(0)?.picture,
                contentScale = ContentScale.Crop,
                contentDescription = "video image"
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd),
                            startY = 80f
                        )
                    )
            )

            // Video badge (top-left)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(VideoBadge.copy(alpha = 0.9f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "VIDEO",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Play button (Center)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play video",
                    tint = VideoBadge,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Bookmark icon button (top-right)
            IconButton(
                onClick = onBookmarkRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isBookmarked)
                        "Remove bookmark"
                    else
                        "Bookmark removed, will disappear on next visit",
                    tint = bookmarkColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                )
            }

            // Video info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = video.user.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = TimeFormatUtils.formatDuration(video.duration),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}


@Composable
fun BookmarkPhotoGridItem(
    photo: Photo,
    isBookmarked: Boolean,
    onBookmarkRemove: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.2f else 1f,
        animationSpec = bookmarkAnimationSpec,
        label = "bookmark_scale"
    )

    val bookmarkColor by animateColorAsState(
        targetValue = if (isBookmarked) BookmarkActive else Color.White.copy(alpha = 0.8f),
        label = "bookmark_color"
    )

    // 북마크 해제 시 투명도 효과 (아이템이 곧 사라질 것임을 시각적으로 표시)
    val cardAlpha by animateFloatAsState(
        targetValue = if (isBookmarked) BOOKMARKED_ALPHA else UNBOOKMARKED_ALPHA,
        animationSpec = bookmarkAnimationSpec,
        label = "card_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer { alpha = cardAlpha }
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo image
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                model = photo.src.medium ?: photo.src.original,
                contentScale = ContentScale.Crop,
                contentDescription = "photo image"
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd),
                            startY = 80f
                        )
                    )
            )

            // Photo badge (top-left)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PhotoBadge.copy(alpha = 0.9f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "PHOTO",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Bookmark icon button (top-right)
            IconButton(
                onClick = onBookmarkRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isBookmarked)
                        "Remove bookmark"
                    else
                        "Bookmark removed, will disappear on next visit",
                    tint = bookmarkColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                )
            }

            // Photo info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = photo.photographer,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "BookmarkMediaGrid - Mixed Content")
@Composable
fun PreviewBookmarkMediaGrid() {
    GrensilVideoListTheme {
        BookmarkMediaGrid(
            videos = listOf(
                Video(
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
                            picture = "https://images.pexels.com/videos/3571264/free-video-3571264.jpg",
                            nr = 0
                        )
                    )
                ),
                Video(
                    id = 2,
                    width = 400,
                    height = 200,
                    url = "url2",
                    image = "image2",
                    duration = 120,
                    user = VideoUser(
                        id = 223,
                        name = "Jane Creator",
                        url = "url2"
                    ),
                    videoFiles = listOf(
                        VideoFile(
                            id = 345,
                            quality = "4K",
                            fileType = "fileType",
                            width = 400,
                            height = 200,
                            link = "link2"
                        )
                    ),
                    videoPictures = listOf(
                        VideoPicture(
                            id = 2,
                            picture = "https://images.pexels.com/videos/3571264/free-video-3571264.jpg",
                            nr = 0
                        )
                    )
                )
            ),
            photos = listOf(
                Photo(
                    id = 1,
                    width = 1920,
                    height = 1080,
                    url = "https://www.pexels.com/photo/1",
                    photographer = "John Doe",
                    photographerUrl = "https://www.pexels.com/@johndoe",
                    avgColor = "#000000",
                    src = PhotoSrc(
                        original = "https://images.pexels.com/photos/1/pexels-photo-1.jpeg",
                        large2x = "",
                        large = "",
                        medium = "",
                        small = "",
                        portrait = "",
                        landscape = "",
                        tiny = ""
                    ),
                    alt = "Sample photo"
                ),
                Photo(
                    id = 2,
                    width = 1920,
                    height = 1080,
                    url = "https://www.pexels.com/photo/2",
                    photographer = "Jane Smith",
                    photographerUrl = "https://www.pexels.com/@janesmith",
                    avgColor = "#FFFFFF",
                    src = PhotoSrc(
                        original = "https://images.pexels.com/photos/2/pexels-photo-2.jpeg",
                        large2x = "",
                        large = "",
                        medium = "",
                        small = "",
                        portrait = "",
                        landscape = "",
                        tiny = ""
                    ),
                    alt = "Another sample photo"
                )
            ),
            videoBookmarkStates = mapOf(1L to true, 2L to false),
            photoBookmarkStates = mapOf(1L to true, 2L to false),
            onVideoBookmarkRemove = {},
            onPhotoBookmarkRemove = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "BookmarkMediaGrid - Empty")
@Composable
fun PreviewBookmarkMediaGridEmpty() {
    GrensilVideoListTheme {
        BookmarkMediaGrid(
            videos = emptyList(),
            photos = emptyList(),
            videoBookmarkStates = emptyMap(),
            photoBookmarkStates = emptyMap(),
            onVideoBookmarkRemove = {},
            onPhotoBookmarkRemove = {}
        )
    }
}
