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
import com.example.domain.usecase.photo.IsPhotoSavedUseCase
import com.example.domain.usecase.photo.SavePhotoUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.IsVideoSavedUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val isPhotoSavedUseCase: IsPhotoSavedUseCase
) : ViewModel() {

    val mediaPagingData: Flow<PagingData<MediaItem>> =
        getMediaPagingDataUseCase()
            .cachedIn(viewModelScope)

    // Track bookmark state changes (null = no change from MediaItem.isBookmarked)
    private val _bookmarkedVideos = MutableStateFlow<Map<Long, Boolean?>>(emptyMap())
    val bookmarkedVideos: StateFlow<Map<Long, Boolean?>> = _bookmarkedVideos

    private val _bookmarkedPhotos = MutableStateFlow<Map<Long, Boolean?>>(emptyMap())
    val bookmarkedPhotos: StateFlow<Map<Long, Boolean?>> = _bookmarkedPhotos

    fun toggleVideoBookmark(video: Video) {
        viewModelScope.launch {
            val isSaved = isVideoSavedUseCase(video.id)
            if (isSaved) {
                deleteVideoUseCase(video)
                _bookmarkedVideos.value = _bookmarkedVideos.value + (video.id to false)
            } else {
                saveVideoUseCase(video)
                _bookmarkedVideos.value = _bookmarkedVideos.value + (video.id to true)
            }
        }
    }

    fun togglePhotoBookmark(photo: Photo) {
        viewModelScope.launch {
            val isSaved = isPhotoSavedUseCase(photo.id)
            if (isSaved) {
                deletePhotoUseCase(photo)
                _bookmarkedPhotos.value = _bookmarkedPhotos.value + (photo.id to false)
            } else {
                savePhotoUseCase(photo)
                _bookmarkedPhotos.value = _bookmarkedPhotos.value + (photo.id to true)
            }
        }
    }
}