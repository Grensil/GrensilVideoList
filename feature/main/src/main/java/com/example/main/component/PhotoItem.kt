package com.example.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import coil.compose.AsyncImage
import com.example.designsystem.theme.BookmarkActive
import com.example.designsystem.theme.DarkCard
import com.example.designsystem.theme.GradientEnd
import com.example.designsystem.theme.GradientStart
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.designsystem.theme.PhotoBadge
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc

@Composable
fun PhotoItem(
    photo: Photo,
    isBookmarked: Boolean = false,
    onBookmarkClick: (Photo) -> Unit = {}
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
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo image
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                model = photo.src.large ?: photo.src.original,
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
                            startY = 100f
                        )
                    )
            )

            // Photo badge (Top Left)
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PhotoBadge.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "PHOTO",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Bookmark button (Top Right)
            IconButton(
                onClick = { onBookmarkClick(photo) },
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

            // Bottom info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = photo.photographer,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Resolution chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${photo.width}x${photo.height}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "PhotoItem - Not Bookmarked")
@Composable
fun PreviewPhotoItem() {
    GrensilVideoListTheme {
        PhotoItem(
            photo = Photo(
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
            isBookmarked = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "PhotoItem - Bookmarked")
@Composable
fun PreviewPhotoItemBookmarked() {
    GrensilVideoListTheme {
        PhotoItem(
            photo = Photo(
                id = 2,
                width = 3840,
                height = 2160,
                url = "https://www.pexels.com/photo/2",
                photographer = "Jane Smith Photography Studio",
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
            ),
            isBookmarked = true
        )
    }
}