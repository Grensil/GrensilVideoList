package com.example.domain.usecase.photo

import com.example.domain.model.Photo
import com.example.domain.repository.local.PhotoRepository
import javax.inject.Inject

class SavePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) {
        photoRepository.savePhoto(photo)
    }
}
