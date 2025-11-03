package com.example.domain.usecase.photo

import com.example.domain.repository.local.PhotoRepository
import javax.inject.Inject

class IsPhotoSavedUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photoId: Long): Boolean {
        return photoRepository.isPhotoSaved(photoId)
    }
}
