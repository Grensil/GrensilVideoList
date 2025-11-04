package com.example.domain.usecase.video

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookmarkedVideosStateUseCase @Inject constructor(
    private val getSavedVideosUseCase: GetSavedVideosUseCase
) {
    operator fun invoke(): Flow<Map<Long, Boolean>> {
        return getSavedVideosUseCase()
            .map { savedVideos ->
                savedVideos.associate { it.id to true }
            }
    }
}
