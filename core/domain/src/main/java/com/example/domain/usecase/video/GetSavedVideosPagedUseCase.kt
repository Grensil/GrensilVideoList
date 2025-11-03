package com.example.domain.usecase.video

import androidx.paging.PagingData
import com.example.domain.model.Video
import com.example.domain.repository.local.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedVideosPagedUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<PagingData<Video>> {
        return videoRepository.getSavedVideosPaged()
    }
}
