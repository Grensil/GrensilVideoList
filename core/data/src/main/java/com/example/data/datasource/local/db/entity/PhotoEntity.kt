package com.example.data.datasource.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.PhotoSrc

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val avgColor: String,
    val src: PhotoSrc,
    val alt: String
)
