package com.example.bookmark.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.designsystem.theme.PurpleGrey40
import com.example.designsystem.theme.PurpleGrey80
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser

@Composable
fun BookmarkMediaGrid(
    videos: List<Video>,
    photos: List<Photo>,
    videoBookmarkStates: Map<Long, Boolean>,
    photoBookmarkStates: Map<Long, Boolean>,
    onVideoBookmarkRemove: (Video) -> Unit,
    onPhotoBookmarkRemove: (Photo) -> Unit,
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
                onBookmarkRemove = { onVideoBookmarkRemove(video) }
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
                onBookmarkRemove = { onPhotoBookmarkRemove(photo) }
            )
        }
    }
}

@Composable
fun BookmarkVideoGridItem(
    video: Video,
    isBookmarked: Boolean,
    onBookmarkRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video thumbnail
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleGrey80),
                model = video.videoPictures?.get(0)?.picture,
                contentScale = ContentScale.Crop,
                contentDescription = "video image"
            )

            // Video icon overlay (top-left)
            Image(
                painter = painterResource(id = com.example.bookmark.R.drawable.icon_video),
                contentDescription = "video icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .size(32.dp)
                    .zIndex(1f)
                    .background(PurpleGrey40.copy(alpha = 0.5f))
                    .align(Alignment.TopStart),
                alignment = Alignment.TopStart
            )

            // Bookmark icon button (top-right)
            IconButton(
                onClick = onBookmarkRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .zIndex(2f)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Remove bookmark",
                    tint = if (isBookmarked)
                        Color(0xFFFFD700)  // Gold for bookmarked
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),  // Semi-transparent for unbookmarked
                    modifier = Modifier.size(28.dp)
                )
            }

            // Video info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(8.dp)
            ) {
                Text(
                    text = video.user.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Duration: ${video.duration}s",
                    color = MaterialTheme.colorScheme.onSurface,
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo image
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleGrey80),
                model = photo.src.original,
                contentScale = ContentScale.Crop,
                contentDescription = "photo image"
            )

            // Photo icon overlay (top-left)
            Image(
                painter = painterResource(id = com.example.bookmark.R.drawable.icon_photo),
                contentDescription = "photo icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .size(32.dp)
                    .zIndex(1f)
                    .background(PurpleGrey40.copy(alpha = 0.5f))
                    .align(Alignment.TopStart),
                alignment = Alignment.TopStart
            )

            // Bookmark icon button (top-right)
            IconButton(
                onClick = onBookmarkRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .zIndex(2f)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Remove bookmark",
                    tint = if (isBookmarked)
                        Color(0xFFFFD700)  // Gold for bookmarked
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),  // Semi-transparent for unbookmarked
                    modifier = Modifier.size(28.dp)
                )
            }

            // Photo info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Photographer: ${photo.photographer}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "BookmarkMediaGrid - Mixed Content")
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

@Preview(showBackground = true, name = "BookmarkMediaGrid - Empty")
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
