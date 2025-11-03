package com.example.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Video


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
                text = "ID: ${video.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "User: ${video.user.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Duration: ${video.duration}s",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Size: ${video.width}x${video.height}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}