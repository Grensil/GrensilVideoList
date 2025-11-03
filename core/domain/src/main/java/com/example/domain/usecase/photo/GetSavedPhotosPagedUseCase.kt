package com.example.domain.usecase.photo

import androidx.paging.PagingData
import com.example.domain.model.Photo
import com.example.domain.repository.local.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedPhotosPagedUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(): Flow<PagingData<Photo>> {
        return photoRepository.getSavedPhotosPaged()
    }
}
