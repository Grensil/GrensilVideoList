package com.example.domain.usecase.video

import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<List<Video>> {
        return videoRepository.getSavedVideos()
    }
}
