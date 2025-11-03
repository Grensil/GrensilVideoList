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

    fun removeVideoBookmark(video: Video) {
        viewModelScope.launch {
            deleteVideoUseCase(video)
        }
    }

    fun removePhotoBookmark(photo: Photo) {
        viewModelScope.launch {
            deletePhotoUseCase(photo)
        }
    }
}