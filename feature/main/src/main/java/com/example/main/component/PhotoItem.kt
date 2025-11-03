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
import com.example.domain.model.Photo


@Composable
fun PhotoItem(photo: Photo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "PHOTO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "ID: ${photo.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Photographer: ${photo.photographer}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Size: ${photo.width}x${photo.height}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Alt: ${photo.alt}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}