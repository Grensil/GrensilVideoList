package com.example.data.model

import com.example.data.datasource.local.db.entity.PhotoEntity
import com.example.data.datasource.local.db.entity.VideoEntity
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
import com.example.network.model.PhotoDto
import com.example.network.model.PhotoSrcDto
import com.example.network.model.VideoDto
import com.example.network.model.VideoFileDto
import com.example.network.model.VideoPictureDto
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

fun VideoPictureDto.toDomain(): VideoPicture {
    return VideoPicture(
        id = id,
        picture = picture,
        nr = nr
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
        videoFiles = video_files.map { it.toDomain() },
        videoPictures = video_pictures?.map { it.toDomain() }
    )
}

fun PhotoSrcDto.toDomain(): PhotoSrc {
    return PhotoSrc(
        original = original,
        large2x = large2x,
        large = large,
        medium = medium,
        small = small,
        portrait = portrait,
        landscape = landscape,
        tiny = tiny
    )
}

fun PhotoDto.toDomain(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        url = url,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        src = src.toDomain(),
        alt = alt
    )
}

// Entity to Domain mappers
fun VideoEntity.toDomain(): Video {
    return Video(
        id = id,
        width = width,
        height = height,
        url = url,
        image = image,
        duration = duration,
        user = user,
        videoFiles = videoFiles,
        videoPictures = videoPictures
    )
}

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        url = url,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        src = src,
        alt = alt
    )
}

// Domain to Entity mappers
fun Video.toEntity(): VideoEntity {
    return VideoEntity(
        id = id,
        width = width,
        height = height,
        url = url,
        image = image,
        duration = duration,
        user = user,
        videoFiles = videoFiles,
        videoPictures = videoPictures
    )
}

fun Photo.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        width = width,
        height = height,
        url = url,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        src = src,
        alt = alt
    )
}