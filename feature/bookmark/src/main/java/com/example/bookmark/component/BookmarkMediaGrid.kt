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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.designsystem.theme.PurpleGrey40
import com.example.designsystem.theme.PurpleGrey80
import com.example.domain.model.Photo
import com.example.domain.model.Video

sealed class BookmarkMediaItem {
    data class VideoItem(val video: Video, val isBookmarked: Boolean) : BookmarkMediaItem()
    data class PhotoItem(val photo: Photo, val isBookmarked: Boolean) : BookmarkMediaItem()
}

@Composable
fun BookmarkMediaGrid(
    videos: List<Video>,
    photos: List<Photo>,
    videoBookmarkStates: Map<Long, Boolean>,
    photoBookmarkStates: Map<Long, Boolean>,
    onVideoBookmarkRemove: (Video) -> Unit,
    onPhotoBookmarkRemove: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Combine videos and photos into a single list
    val mediaItems = buildList {
        addAll(videos.map { video ->
            val isBookmarked = videoBookmarkStates[video.id] ?: true // 기본값은 true (리스트에 있으면 북마크됨)
            BookmarkMediaItem.VideoItem(video, isBookmarked)
        })
        addAll(photos.map { photo ->
            val isBookmarked = photoBookmarkStates[photo.id] ?: true // 기본값은 true (리스트에 있으면 북마크됨)
            BookmarkMediaItem.PhotoItem(photo, isBookmarked)
        })
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        items(mediaItems) { item ->
            when (item) {
                is BookmarkMediaItem.VideoItem -> BookmarkVideoGridItem(
                    video = item.video,
                    isBookmarked = item.isBookmarked,
                    onBookmarkRemove = { onVideoBookmarkRemove(item.video) }
                )
                is BookmarkMediaItem.PhotoItem -> BookmarkPhotoGridItem(
                    photo = item.photo,
                    isBookmarked = item.isBookmarked,
                    onBookmarkRemove = { onPhotoBookmarkRemove(item.photo) }
                )
            }
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
                    .background(PurpleGrey40)
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
                    tint = if (isBookmarked) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Video info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = video.user.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Duration: ${video.duration}s",
                    color = Color.White,
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
                    .background(PurpleGrey40)
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
                    tint = if (isBookmarked) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Photo info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Photographer: ${photo.photographer}",
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
