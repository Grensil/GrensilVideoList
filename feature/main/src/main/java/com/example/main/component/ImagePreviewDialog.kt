package com.example.main.component

import androidx.compose.runtime.Composable
import com.example.designsystem.component.ImagePreviewDialog as SharedImagePreviewDialog
import com.example.domain.model.Photo

@Composable
fun ImagePreviewDialog(
    photo: Photo,
    onDismiss: () -> Unit
) {
    SharedImagePreviewDialog(
        imageUrl = photo.src.large2x ?: photo.src.large ?: photo.src.original,
        contentDescription = photo.alt,
        onDismiss = onDismiss
    )
}
