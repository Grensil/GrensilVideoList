package com.example.grensilvideolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Video
import com.example.domain.usecase.GetPopularVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val getPopularVideosUseCase: GetPopularVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Initial)
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun loadPopularVideos(perPage: Int = 20) {
        viewModelScope.launch {
            _uiState.value = VideoUiState.Loading

            getPopularVideosUseCase(perPage)
                .onSuccess { videos ->
                    _uiState.value = VideoUiState.Success(videos)
                }
                .onFailure { error ->
                    _uiState.value = VideoUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}

sealed class VideoUiState {
    object Initial : VideoUiState()
    object Loading : VideoUiState()
    data class Success(val videos: List<Video>) : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}
