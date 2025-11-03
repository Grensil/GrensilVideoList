package com.example.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.domain.model.MediaItem
import com.example.main.component.ErrorContent
import com.example.main.component.PhotoItem
import com.example.main.component.VideoItem


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