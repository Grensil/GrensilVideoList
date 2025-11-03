package com.example.grensilvideolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.domain.model.MediaItem
import com.example.domain.model.Photo
import com.example.domain.model.Video
import com.example.grensilvideolist.ui.theme.GrensilVideoListTheme
import com.example.grensilvideolist.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrensilVideoListTheme {
                MediaListScreen()
            }
        }
    }
}

@Composable
fun MediaListScreen(
    viewModel: VideoViewModel = hiltViewModel()
) {
    val mediaPagingItems = viewModel.mediaPagingData.collectAsLazyPagingItems()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MediaList(mediaPagingItems = mediaPagingItems)
        }
    }
}

@Composable
fun MediaList(mediaPagingItems: LazyPagingItems<MediaItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mediaPagingItems.itemCount) { index ->
            mediaPagingItems[index]?.let { mediaItem ->
                when (mediaItem) {
                    is MediaItem.VideoItem -> VideoItem(video = mediaItem.video)
                    is MediaItem.PhotoItem -> PhotoItem(photo = mediaItem.photo)
                }
            }
        }

        // Loading state
        mediaPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val error = (loadState.refresh as LoadState.Error).error
                    item {
                        ErrorContent(
                            message = error.message ?: "Unknown error",
                            onRetryClick = { retry() }
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    val error = (loadState.append as LoadState.Error).error
                    item {
                        ErrorContent(
                            message = error.message ?: "Unknown error",
                            onRetryClick = { retry() }
                        )
                    }
                }
            }
        }
    }
}

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

@Composable
fun ErrorContent(message: String, onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: $message",
                color = MaterialTheme.colorScheme.error
            )
            androidx.compose.material3.Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}
