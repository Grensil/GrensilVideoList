package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.MediaItem
import com.example.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaPagingDataUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<PagingData<MediaItem>> {
        return videoRepository.getMediaPagingData()
    }
}
