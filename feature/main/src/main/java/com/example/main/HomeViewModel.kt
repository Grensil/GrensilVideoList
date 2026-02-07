package com.example.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.MediaItem
import com.example.domain.model.Photo
import com.example.domain.model.Video
import com.example.domain.usecase.media.GetMediaPagingDataUseCase
import com.example.domain.usecase.photo.DeletePhotoUseCase
import com.example.domain.usecase.photo.GetBookmarkedPhotosStateUseCase
import com.example.domain.usecase.photo.IsPhotoSavedUseCase
import com.example.domain.usecase.photo.SavePhotoUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.GetBookmarkedVideosStateUseCase
import com.example.domain.usecase.video.IsVideoSavedUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
import com.example.player.VideoPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMediaPagingDataUseCase: GetMediaPagingDataUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val isVideoSavedUseCase: IsVideoSavedUseCase,
    private val isPhotoSavedUseCase: IsPhotoSavedUseCase,
    private val getBookmarkedVideosStateUseCase: GetBookmarkedVideosStateUseCase,
    private val getBookmarkedPhotosStateUseCase: GetBookmarkedPhotosStateUseCase,
    val videoPlayerManager: VideoPlayerManager
) : ViewModel() {

    // 현재 프리뷰 재생 중인 비디오 ID
    private val _currentPlayingVideoId = MutableStateFlow<Long?>(null)
    val currentPlayingVideoId: StateFlow<Long?> = _currentPlayingVideoId.asStateFlow()

    // 디테일 전환 시점 (스크롤 debounce와의 경쟁 방지)
    private var detailNavigationTime = 0L

    // 재생 진행률
    val playbackProgress: StateFlow<Float> = videoPlayerManager.playbackState
        .map { it.progress }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

    // 비디오가 실제 재생 중인지 (첫 프레임 렌더링 완료 포함)
    val isVideoActuallyPlaying: StateFlow<Boolean> = videoPlayerManager.playbackState
        .map { it.isPlaying && it.playbackState == com.example.player.PlaybackState.STATE_READY && it.isFirstFrameRendered }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // 남은 시간 (초)
    val remainingSeconds: StateFlow<Int> = videoPlayerManager.playbackState
        .map { state ->
            if (state.duration > 0) {
                ((state.duration - state.currentPosition) / 1000).toInt().coerceAtLeast(0)
            } else 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val mediaPagingData: Flow<PagingData<MediaItem>> =
        getMediaPagingDataUseCase().cachedIn(viewModelScope)

    // DB Flow를 hot stream으로 변환하여 북마크 상태 추적
    val bookmarkedVideos: StateFlow<Map<Long, Boolean>> = getBookmarkedVideosStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val bookmarkedPhotos: StateFlow<Map<Long, Boolean>> = getBookmarkedPhotosStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun toggleVideoBookmark(video: Video) {
        viewModelScope.launch {
            val isSaved = isVideoSavedUseCase(video.id)
            if (isSaved) {
                deleteVideoUseCase(video)
            } else {
                saveVideoUseCase(video)
            }
        }
    }

    fun togglePhotoBookmark(photo: Photo) {
        viewModelScope.launch {
            val isSaved = isPhotoSavedUseCase(photo.id)
            if (isSaved) {
                deletePhotoUseCase(photo)
            } else {
                savePhotoUseCase(photo)
            }
        }
    }

    fun onCenterVideoChanged(video: Video?) {
        // 디테일 전환 직후 스크롤 debounce가 프리뷰를 시작하면
        // prepareForDetail()을 덮어쓰므로 500ms 동안 무시
        if (System.currentTimeMillis() - detailNavigationTime < 500L) return

        if (video == null) {
            stopPreview()
            return
        }

        if (_currentPlayingVideoId.value == video.id) {
            return // 같은 비디오면 스킵
        }

        videoPlayerManager.resetFirstFrame()
        _currentPlayingVideoId.value = video.id
        startPreviewPlayback(video)
    }

    private fun startPreviewPlayback(video: Video) {
        val url = video.videoFiles.maxByOrNull { it.width * it.height }?.link ?: return
        videoPlayerManager.prepare(
            videoId = video.id,
            url = url,
            muted = true,
            autoPlay = true
        )
    }

    fun stopPreview() {
        _currentPlayingVideoId.value = null
        videoPlayerManager.stop()
    }

    fun onVideoClicked(video: Video) {
        detailNavigationTime = System.currentTimeMillis()
        _currentPlayingVideoId.value = null
        videoPlayerManager.setCurrentVideo(video)
        videoPlayerManager.prepareForTransition()
    }

    override fun onCleared() {
        super.onCleared()
        videoPlayerManager.release()
    }
}
