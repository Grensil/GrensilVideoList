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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val getBookmarkedPhotosStateUseCase: GetBookmarkedPhotosStateUseCase
) : ViewModel() {

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
}
