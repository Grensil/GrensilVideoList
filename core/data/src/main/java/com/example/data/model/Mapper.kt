package com.example.data.model

import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoUser
import com.example.network.model.VideoDto
import com.example.network.model.VideoFileDto
import com.example.network.model.VideoUserDto

fun VideoUserDto.toDomain(): VideoUser {
    return VideoUser(
        id = id,
        name = name,
        url = url
    )
}

fun VideoFileDto.toDomain(): VideoFile {
    return VideoFile(
        id = id,
        quality = quality,
        fileType = fileType,
        width = width,
        height = height,
        link = link
    )
}

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        width = width,
        height = height,
        url = url,
        image = image,
        duration = duration,
        user = user.toDomain(),
        videoFiles = videoFiles.map { it.toDomain() }
    )
}