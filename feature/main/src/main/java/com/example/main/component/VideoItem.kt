package com.example.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.designsystem.theme.PurpleGrey40
import com.example.designsystem.theme.PurpleGrey80
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
import com.example.main.R


@Composable
fun VideoItem(
    video: Video,
    isBookmarked: Boolean = false,
    onBookmarkClick: (Video) -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp)
    ) {
        Box(contentAlignment = Alignment.TopCenter) {

            Image(
                painter = painterResource(id = R.drawable.icon_video),
                contentDescription = "photo icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .size(32.dp)
                    .zIndex(1f)
                    .background(PurpleGrey40)
                    .align(Alignment.TopStart),
                alignment = Alignment.TopStart
            )

            // Bookmark icon button (Top Right)
            IconButton(
                onClick = { onBookmarkClick(video) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .zIndex(2f)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) Color(0xFFFFD700) else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PurpleGrey80),
                model = video.videoPictures?.get(0)?.picture,
                contentScale = ContentScale.Crop,
                contentDescription = "video  image"
            )

            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {

                Text(
                    text = video.user.name,
                    color = PurpleGrey80
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Duration: ${video.duration}s",
                    color = PurpleGrey80
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVideoItem() {
    GrensilVideoListTheme {
        VideoItem(
            Video(
                id = 1,
                width = 400,
                height = 200,
                url = "url",
                image = "image",
                duration = 55,
                user = VideoUser(
                    id = 222,
                    name = "name",
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
            )
        )
    }
}
