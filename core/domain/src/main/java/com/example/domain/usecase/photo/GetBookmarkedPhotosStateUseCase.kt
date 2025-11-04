package com.example.domain.usecase.photo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookmarkedPhotosStateUseCase @Inject constructor(
    private val getSavedPhotosUseCase: GetSavedPhotosUseCase
) {
    operator fun invoke(): Flow<Map<Long, Boolean>> {
        return getSavedPhotosUseCase()
            .map { savedPhotos ->
                savedPhotos.associate { it.id to true }
            }
    }
}
