package com.example.main.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Video
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.IsVideoSavedUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
import com.example.main.player.PlaybackState
import com.example.main.player.VideoPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val playerManager: VideoPlayerManager,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val isVideoSavedUseCase: IsVideoSavedUseCase
) : ViewModel() {

    val videoId: Long = savedStateHandle.get<Long>("videoId") ?: -1L

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playerManager.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaybackState()
        )

    init {
        viewModelScope.launch {
            _isBookmarked.value = isVideoSavedUseCase(videoId)
        }
    }

    fun setVideo(video: Video) {
        _video.value = video
        // 비디오가 이미 프리뷰에서 재생 중이면 음소거만 해제
        if (playerManager.playbackState.value.videoId == video.id) {
            playerManager.setMuted(false)
        } else {
            // 새로운 비디오면 처음부터 재생
            val url = video.videoFiles.maxByOrNull { it.width * it.height }?.link ?: return
            playerManager.prepare(
                videoId = video.id,
                url = url,
                muted = false,
                autoPlay = true
            )
        }
    }

    fun togglePlayPause() {
        val state = playerManager.playbackState.value
        if (state.isPlaying) {
            playerManager.pause()
        } else {
            playerManager.play()
        }
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun exitFullscreen() {
        _isFullscreen.value = false
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val currentVideo = _video.value ?: return@launch
            if (_isBookmarked.value) {
                deleteVideoUseCase(currentVideo)
                _isBookmarked.value = false
            } else {
                saveVideoUseCase(currentVideo)
                _isBookmarked.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel이 정리될 때 음소거로 돌아가기 (프리뷰용)
        playerManager.setMuted(true)
    }
}
