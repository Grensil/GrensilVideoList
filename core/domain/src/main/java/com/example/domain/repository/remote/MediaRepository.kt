package com.example.domain.repository.remote

import androidx.paging.PagingData
import com.example.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository for combined Media operations (Video + Photo)
 * Used for features that need both Video and Photo together, like mixed paging
 */
interface MediaRepository {
    fun getMediaPagingData(): Flow<PagingData<MediaItem>>
}