package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val donghuaId: String,
    val episodeId: String,
    val episodeNumber: Int,
    val playbackPositionSeconds: Int,
    val totalDurationSeconds: Int,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val donghuaId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String, // donghuaId_episodeId_quality
    val donghuaId: String,
    val donghuaTitle: String,
    val episodeId: String,
    val episodeNumber: Int,
    val episodeTitle: String,
    val quality: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: String,
    val speedKbps: Int = 0,
    val isMovie: Boolean = false,
    val localFilePath: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_subscription")
data class UserSubscriptionEntity(
    @PrimaryKey val id: Int = 1,
    val isVip: Boolean = false,
    val planId: String = "",
    val planName: String = "",
    val startDate: Long = 0L,
    val expiryDate: Long = 0L,
    val paymentMethod: String = ""
)
