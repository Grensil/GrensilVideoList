package com.example.data.datasource.local.db.converter

import androidx.room.TypeConverter
import com.example.domain.model.PhotoSrc
import com.google.gson.Gson

class PhotoConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromPhotoSrc(value: PhotoSrc): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPhotoSrc(value: String): PhotoSrc {
        return gson.fromJson(value, PhotoSrc::class.java)
    }
}
