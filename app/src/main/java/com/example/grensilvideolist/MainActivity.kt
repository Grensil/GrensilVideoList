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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.Video
import com.example.grensilvideolist.ui.theme.GrensilVideoListTheme
import com.example.grensilvideolist.viewmodel.VideoUiState
import com.example.grensilvideolist.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrensilVideoListTheme {
                VideoListScreen()
            }
        }
    }
}

@Composable
fun VideoListScreen(
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is VideoUiState.Initial -> {
                    InitialContent(onLoadClick = { viewModel.loadPopularVideos() })
                }
                is VideoUiState.Loading -> {
                    LoadingContent()
                }
                is VideoUiState.Success -> {
                    VideoList(videos = state.videos)
                }
                is VideoUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetryClick = { viewModel.loadPopularVideos() }
                    )
                }
            }
        }
    }
}

@Composable
fun InitialContent(onLoadClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onLoadClick) {
            Text("Load Popular Videos")
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun VideoList(videos: List<Video>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videos) { video ->
            VideoItem(video = video)
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
            Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}
