package com.example.bookmark

import androidx.lifecycle.ViewModel
import com.example.domain.usecase.media.GetMediaPagingDataUseCase
import com.example.domain.usecase.photo.DeletePhotoUseCase
import com.example.domain.usecase.photo.SavePhotoUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.GetSavedVideosUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getSavedPhotoUseCase: GetMediaPagingDataUseCase,
    private val getSavedVideoUseCase: GetSavedVideosUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModel() {


}