package com.example.domain.usecase

import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
import javax.inject.Inject

class GetPopularVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(perPage: Int = 1): Result<List<Video>> {
        return videoRepository.getPopularVideos(perPage)
    }
}
