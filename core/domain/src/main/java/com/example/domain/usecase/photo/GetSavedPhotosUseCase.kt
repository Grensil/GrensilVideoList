package com.example.domain.usecase.photo

import com.example.domain.model.Photo
import com.example.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(): Flow<List<Photo>> {
        return photoRepository.getSavedPhotos()
    }
}
