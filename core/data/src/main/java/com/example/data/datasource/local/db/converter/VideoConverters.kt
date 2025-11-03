package com.example.data.datasource.local.db.converter

import androidx.room.TypeConverter
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromVideoUser(value: VideoUser): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoUser(value: String): VideoUser {
        return gson.fromJson(value, VideoUser::class.java)
    }

    @TypeConverter
    fun fromVideoFileList(value: List<VideoFile>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoFileList(value: String): List<VideoFile> {
        val type = object : TypeToken<List<VideoFile>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromVideoPictureList(value: List<VideoPicture>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toVideoPictureList(value: String?): List<VideoPicture>? {
        if (value == null) return null
        val type = object : TypeToken<List<VideoPicture>>() {}.type
        return gson.fromJson(value, type)
    }
}
