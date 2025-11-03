package com.example.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.designsystem.theme.PurpleGrey80
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoUser


@Composable
fun VideoItem(video: Video) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "VIDEO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "User: ${video.user.name}",
                style = MaterialTheme.typography.bodyMedium
            )

            AsyncImage(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PurpleGrey80),
                model = video.videoPictures?.get(0)?.picture,
                contentScale = ContentScale.Crop,
                contentDescription = "article image"
            )

            Text(
                text = "Duration: ${video}s",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewVideoItem() {
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
        )
    )
}
