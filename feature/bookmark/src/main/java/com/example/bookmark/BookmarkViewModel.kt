package com.example.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Photo
import com.example.domain.model.Video
import com.example.domain.usecase.photo.DeletePhotoUseCase
import com.example.domain.usecase.photo.GetSavedPhotosUseCase
import com.example.domain.usecase.photo.SavePhotoUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.GetSavedVideosUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
import com.example.player.VideoPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getSavedPhotosUseCase: GetSavedPhotosUseCase,
    private val getSavedVideosUseCase: GetSavedVideosUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val videoPlayerManager: VideoPlayerManager
) : ViewModel() {

    // UI 상태 - 세션 중에는 삭제해도 유지됨
    private val _uiVideos = MutableStateFlow<List<Video>>(emptyList())
    val uiVideos: StateFlow<List<Video>> = _uiVideos.asStateFlow()

    private val _uiPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val uiPhotos: StateFlow<List<Photo>> = _uiPhotos.asStateFlow()

    // 북마크 상태 추적 (북마크 아이콘 표시용)
    private val _videoBookmarkStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val videoBookmarkStates: StateFlow<Map<Long, Boolean>> = _videoBookmarkStates.asStateFlow()

    private val _photoBookmarkStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val photoBookmarkStates: StateFlow<Map<Long, Boolean>> = _photoBookmarkStates.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        // DB에서 최신 데이터를 한 번만 로드 (Flow 구독하지 않음)
        // 화면 재진입 시 실제 북마크만 표시
        viewModelScope.launch {
            val videos = getSavedVideosUseCase().first()
            _uiVideos.value = videos
            // 북마크 상태 초기화
            _videoBookmarkStates.value = emptyMap()
        }
        viewModelScope.launch {
            val photos = getSavedPhotosUseCase().first()
            _uiPhotos.value = photos
            // 북마크 상태 초기화
            _photoBookmarkStates.value = emptyMap()
        }
    }

    fun toggleVideoBookmark(video: Video) {
        viewModelScope.launch {
            val currentState = _videoBookmarkStates.value[video.id]
            val isSaved = currentState ?: // 리스트에 있으면 저장되어 있음
            _uiVideos.value.any { it.id == video.id }

            if (isSaved) {
                // 북마크 제거
                deleteVideoUseCase(video)
                _videoBookmarkStates.value = _videoBookmarkStates.value + (video.id to false)
            } else {
                // 북마크 추가
                saveVideoUseCase(video)
                _videoBookmarkStates.value = _videoBookmarkStates.value + (video.id to true)
            }
        }
    }

    fun togglePhotoBookmark(photo: Photo) {
        viewModelScope.launch {
            val currentState = _photoBookmarkStates.value[photo.id]
            val isSaved = currentState ?: // 리스트에 있으면 저장되어 있음
            _uiPhotos.value.any { it.id == photo.id }

            if (isSaved) {
                // 북마크 제거
                deletePhotoUseCase(photo)
                _photoBookmarkStates.value = _photoBookmarkStates.value + (photo.id to false)
            } else {
                // 북마크 추가
                savePhotoUseCase(photo)
                _photoBookmarkStates.value = _photoBookmarkStates.value + (photo.id to true)
            }
        }
    }

    // 하위 호환성을 위해 유지
    fun removeVideoBookmark(video: Video) = toggleVideoBookmark(video)
    fun removePhotoBookmark(photo: Photo) = togglePhotoBookmark(photo)

    // 비디오 클릭 시 네비게이션 전 처리
    fun onVideoClicked(video: Video) {
        videoPlayerManager.setCurrentVideo(video)
    }
}
