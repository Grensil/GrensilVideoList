package com.example.domain.usecase.photo

import com.example.domain.repository.PhotoRepository
import javax.inject.Inject

class IsPhotoSavedUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photoId: Long): Boolean {
        return photoRepository.isPhotoSaved(photoId)
    }
}
