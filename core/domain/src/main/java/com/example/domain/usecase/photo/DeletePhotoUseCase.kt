package com.example.domain.usecase.photo

import com.example.domain.model.Photo
import com.example.domain.repository.PhotoRepository
import javax.inject.Inject

class DeletePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) {
        photoRepository.deletePhoto(photo)
    }

    suspend operator fun invoke(photoId: Long) {
        photoRepository.deletePhotoById(photoId)
    }
}
