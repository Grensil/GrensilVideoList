package com.example.data.datasource.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: VideoUser,
    val videoFiles: List<VideoFile>,
    val videoPictures: List<VideoPicture>?
)
