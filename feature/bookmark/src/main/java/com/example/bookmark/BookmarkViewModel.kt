package com.example.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Photo
import com.example.domain.model.Video
import com.example.domain.usecase.photo.DeletePhotoUseCase
import com.example.domain.usecase.photo.GetSavedPhotosUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.GetSavedVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getSavedPhotosUseCase: GetSavedPhotosUseCase,
    private val getSavedVideosUseCase: GetSavedVideosUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModel() {

    val savedVideos: Flow<List<Video>> = getSavedVideosUseCase()
    val savedPhotos: Flow<List<Photo>> = getSavedPhotosUseCase()

    // Track removed items that should still be visible until tab change
    private val _removedVideoIds = MutableStateFlow<Set<Long>>(emptySet())
    val removedVideoIds: StateFlow<Set<Long>> = _removedVideoIds

    private val _removedPhotoIds = MutableStateFlow<Set<Long>>(emptySet())
    val removedPhotoIds: StateFlow<Set<Long>> = _removedPhotoIds

    fun removeVideoBookmark(video: Video) {
        viewModelScope.launch {
            deleteVideoUseCase(video)
            _removedVideoIds.value = _removedVideoIds.value + video.id
        }
    }

    fun removePhotoBookmark(photo: Photo) {
        viewModelScope.launch {
            deletePhotoUseCase(photo)
            _removedPhotoIds.value = _removedPhotoIds.value + photo.id
        }
    }

    fun clearRemovedItems() {
        _removedVideoIds.value = emptySet()
        _removedPhotoIds.value = emptySet()
    }
}