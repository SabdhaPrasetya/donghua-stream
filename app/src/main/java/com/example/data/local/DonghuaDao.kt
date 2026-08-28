package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonghuaDao {

    // --- Watch History ---
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getAllWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE donghuaId = :donghuaId LIMIT 1")
    fun getWatchHistoryForDonghua(donghuaId: String): Flow<WatchHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE donghuaId = :donghuaId")
    suspend fun deleteWatchHistory(donghuaId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearAllHistory()

    // --- Favorites / Watchlist ---
    @Query("SELECT * FROM favorites ORDER BY addedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE donghuaId = :donghuaId)")
    fun isFavorite(donghuaId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE donghuaId = :donghuaId")
    suspend fun removeFavorite(donghuaId: String)

    // --- Downloads ---
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id LIMIT 1")
    fun getDownloadById(id: String): Flow<DownloadEntity?>

    @Query("SELECT * FROM downloads WHERE donghuaId = :donghuaId")
    fun getDownloadsForDonghua(donghuaId: String): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDownload(download: DownloadEntity)

    @Query("UPDATE downloads SET downloadedBytes = :downloaded, status = :status, speedKbps = :speed WHERE id = :id")
    suspend fun updateDownloadProgress(id: String, downloaded: Long, status: String, speed: Int)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: String)

    @Query("DELETE FROM downloads")
    suspend fun clearAllDownloads()

    // --- Subscription ---
    @Query("SELECT * FROM user_subscription WHERE id = 1 LIMIT 1")
    fun getUserSubscription(): Flow<UserSubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSubscription(subscription: UserSubscriptionEntity)
}
