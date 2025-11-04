package com.example.domain.usecase.video

import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
import javax.inject.Inject

class SaveVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(video: Video) {
        videoRepository.saveVideo(video)
    }
}
