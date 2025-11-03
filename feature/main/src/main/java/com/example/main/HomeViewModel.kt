package com.example.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.MediaItem
import com.example.domain.usecase.GetMediaPagingDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMediaPagingDataUseCase: GetMediaPagingDataUseCase
) : ViewModel() {

    val mediaPagingData: Flow<PagingData<MediaItem>> =
        getMediaPagingDataUseCase()
            .cachedIn(viewModelScope)
}