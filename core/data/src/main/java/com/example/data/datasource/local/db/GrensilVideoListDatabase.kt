package com.example.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.datasource.local.db.converter.PhotoConverters
import com.example.data.datasource.local.db.converter.VideoConverters
import com.example.data.datasource.local.db.dao.PhotoDao
import com.example.data.datasource.local.db.dao.VideoDao
import com.example.data.datasource.local.db.entity.PhotoEntity
import com.example.data.datasource.local.db.entity.VideoEntity

@Database(
    entities = [VideoEntity::class, PhotoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(VideoConverters::class, PhotoConverters::class)
abstract class GrensilVideoListDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun photoDao(): PhotoDao

    companion object {
        const val DATABASE_NAME = "grensil_video_list_database"
    }
}
