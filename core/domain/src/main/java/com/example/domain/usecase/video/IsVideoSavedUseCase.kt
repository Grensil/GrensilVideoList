package com.example.domain.usecase.video

import com.example.domain.repository.local.VideoRepository
import javax.inject.Inject

class IsVideoSavedUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Boolean {
        return videoRepository.isVideoSaved(videoId)
    }
}
