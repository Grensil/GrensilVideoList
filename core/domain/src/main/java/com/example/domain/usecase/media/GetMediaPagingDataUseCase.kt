package com.example.domain.usecase.media

import androidx.paging.PagingData
import com.example.domain.model.MediaItem
import com.example.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaPagingDataUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(): Flow<PagingData<MediaItem>> {
        return mediaRepository.getMediaPagingData()
    }
}