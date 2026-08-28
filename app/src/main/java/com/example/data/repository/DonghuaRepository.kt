package com.example.data.repository

import com.example.data.local.DonghuaDao
import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.Donghua
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.Episode
import com.example.data.model.PaymentMethod
import com.example.data.model.UserSubscription
import com.example.data.model.VideoQuality
import com.example.data.model.VipPlan
import com.example.data.model.WatchHistoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class DonghuaRepository(
    private val dao: DonghuaDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val activeDownloadJobs = ConcurrentHashMap<String, Job>()

    // In-memory simulation states for real-time progress
    private val _downloadProgressMap = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgressMap = _downloadProgressMap.asStateFlow()

    // Dynamic Catalog State with instant releases
    private val _dynamicCatalog = MutableStateFlow<List<Donghua>>(DonghuaCatalog.ALL_DONGHUA)
    val dynamicCatalog = _dynamicCatalog.asStateFlow()

    init {
        // Automatic Cloud Sync from Owner's GitHub Database
        fetchLatestCatalogFromCloud()
    }

    fun fetchLatestCatalogFromCloud() {
        scope.launch(Dispatchers.IO) {
            try {
                val url = java.net.URL("https://raw.githubusercontent.com/SabdhaPrasetya/donghua-database/main/donghua_catalog.json")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"
                
                if (connection.responseCode == 200) {
                    val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = org.json.JSONArray(jsonText)
                    val currentList = _dynamicCatalog.value.toMutableList()
                    
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val id = obj.optString("id")
                        val index = currentList.indexOfFirst { it.id == id }
                        if (index != -1) {
                            val target = currentList[index]
                            val newEpisodesCount = obj.optInt("currentEpisodes", target.currentEpisodes)
                            val isUpdated = obj.optBoolean("isRecentlyUpdated", target.isRecentlyUpdated)
                            val note = obj.optString("latestEpisodeUpdateNote", target.latestEpisodeUpdateNote)
                            
                            // Generate latest episodes if count increased
                            val existingEpisodes = target.episodes.toMutableList()
                            if (newEpisodesCount > existingEpisodes.size) {
                                for (epNum in (existingEpisodes.size + 1)..newEpisodesCount) {
                                    existingEpisodes.add(
                                        Episode(
                                            id = "${target.id}_ep_$epNum",
                                            donghuaId = target.id,
                                            episodeNumber = epNum,
                                            title = "Episode $epNum: Petualangan Lanjutan",
                                            duration = "24:15",
                                            durationSeconds = 1455,
                                            downloadSizeMb = 210,
                                            isVipOnly = true,
                                            synopsis = "Episode $epNum baru saja dirilis secara otomatis oleh sistem bot.",
                                            isNewlyReleased = true,
                                            releaseDateText = "Baru Saja Rilis!"
                                        )
                                    )
                                }
                            }
                            
                            // Reset isRecentlyUpdated if episode count hasn't changed
                            val actuallyUpdated = newEpisodesCount > target.currentEpisodes
                            
                            currentList[index] = target.copy(
                                currentEpisodes = newEpisodesCount,
                                isRecentlyUpdated = if (actuallyUpdated) isUpdated else false,
                                latestEpisodeUpdateNote = if (actuallyUpdated) note else target.latestEpisodeUpdateNote,
                                episodes = existingEpisodes
                            )
                        }
                    }
                    _dynamicCatalog.value = currentList
                }
            } catch (e: Exception) {
                // Fallback to offline pre-bundled catalog if offline/no internet
                android.util.Log.w("DonghuaRepo", "Cloud catalog fetch failed: ${e.message}")
            }
        }
    }

    // --- Catalog Queries ---
    fun getAllDonghua(): List<Donghua> = _dynamicCatalog.value

    fun getDonghuaById(id: String): Donghua? {
        return _dynamicCatalog.value.find { it.id == id }
    }

    fun getDonghuaByDay(day: String): List<Donghua> {
        if (day == "Semua Hari" || day == "Semua") return _dynamicCatalog.value
        return _dynamicCatalog.value.filter { it.uploadDay.equals(day, ignoreCase = true) }
    }

    fun getTodayUploads(): List<Donghua> {
        // Returns donghua scheduled for today or marked with recent update
        return _dynamicCatalog.value.filter { it.isRecentlyUpdated }
    }

    fun releaseNewEpisodeInstantly(donghuaId: String): Donghua? {
        val currentList = _dynamicCatalog.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == donghuaId }
        if (index != -1) {
            val target = currentList[index]
            val newEpNumber = target.currentEpisodes + 1
            val newEpisode = Episode(
                id = "${target.id}_ep_$newEpNumber",
                donghuaId = target.id,
                episodeNumber = newEpNumber,
                title = "Episode $newEpNumber: Pertarungan Puncak Terobosan Baru",
                duration = "24:15",
                durationSeconds = 1455,
                downloadSizeMb = 210,
                isVipOnly = true,
                synopsis = "Episode $newEpNumber baru saja dirilis! Gelombang kekuatan kosmik menyelimuti arena saat pertempuran pamungkas pecah.",
                isNewlyReleased = true,
                releaseDateText = "Baru Saja Rilis!"
            )
            val updatedEpisodes = target.episodes + newEpisode
            val updatedDonghua = target.copy(
                currentEpisodes = newEpNumber,
                isRecentlyUpdated = true,
                latestEpisodeUpdateNote = "Episode $newEpNumber Baru Saja Rilis!",
                episodes = updatedEpisodes
            )
            currentList[index] = updatedDonghua
            _dynamicCatalog.value = currentList
            return updatedDonghua
        }
        return null
    }

    fun getDonghuaByGenre(genre: String): List<Donghua> {
        if (genre == "Semua") return _dynamicCatalog.value
        if (genre == "Film Donghua") return _dynamicCatalog.value.filter { it.isMovie }
        return _dynamicCatalog.value.filter { donghua ->
            donghua.genres.any { it.equals(genre, ignoreCase = true) }
        }
    }

    fun getDonghuaByStudio(studio: String): List<Donghua> {
        if (studio == "Semua Studio") return _dynamicCatalog.value
        return _dynamicCatalog.value.filter { it.studio.contains(studio, ignoreCase = true) }
    }

    fun searchDonghua(
        query: String,
        selectedGenre: String = "Semua",
        selectedStudio: String = "Semua Studio",
        selectedDay: String = "Semua Hari"
    ): List<Donghua> {
        return _dynamicCatalog.value.filter { donghua ->
            val matchesQuery = query.isBlank() ||
                    donghua.title.contains(query, ignoreCase = true) ||
                    donghua.chineseTitle.contains(query, ignoreCase = true) ||
                    donghua.genres.any { it.contains(query, ignoreCase = true) } ||
                    donghua.studio.contains(query, ignoreCase = true)

            val matchesGenre = selectedGenre == "Semua" ||
                    (selectedGenre == "Film Donghua" && donghua.isMovie) ||
                    donghua.genres.any { it.equals(selectedGenre, ignoreCase = true) }

            val matchesStudio = selectedStudio == "Semua Studio" ||
                    donghua.studio.contains(selectedStudio, ignoreCase = true)

            val matchesDay = selectedDay == "Semua Hari" || selectedDay == "Semua" ||
                    donghua.uploadDay.equals(selectedDay, ignoreCase = true)

            matchesQuery && matchesGenre && matchesStudio && matchesDay
        }
    }

    // --- Genre-based Smart Recommendation Engine ---
    fun getPersonalizedRecommendations(): Flow<List<Donghua>> {
        return combine(dao.getAllWatchHistory(), dao.getAllFavorites()) { history, favorites ->
            val watchedIds = history.map { it.donghuaId }.toSet()
            val favoriteIds = favorites.map { it.donghuaId }.toSet()
            val interactedIds = watchedIds + favoriteIds

            val genreFrequency = mutableMapOf<String, Int>()
            interactedIds.forEach { id ->
                getDonghuaById(id)?.genres?.forEach { genre ->
                    genreFrequency[genre] = (genreFrequency[genre] ?: 0) + 1
                }
            }

            if (genreFrequency.isEmpty()) {
                // Default fallback: top rated and popular
                DonghuaCatalog.ALL_DONGHUA.sortedByDescending { it.rating }.take(8)
            } else {
                val topGenres = genreFrequency.entries.sortedByDescending { it.value }.map { it.key }.take(3)
                DonghuaCatalog.ALL_DONGHUA
                    .filter { it.id !in watchedIds }
                    .sortedByDescending { donghua ->
                        val score = donghua.genres.count { it in topGenres } * 2.0 + donghua.rating
                        score
                    }
                    .ifEmpty { DonghuaCatalog.ALL_DONGHUA.sortedByDescending { it.rating } }
                    .take(8)
            }
        }
    }

    fun getRelatedDonghua(donghua: Donghua): List<Donghua> {
        return DonghuaCatalog.ALL_DONGHUA
            .filter { it.id != donghua.id }
            .sortedByDescending { other ->
                val commonGenres = other.genres.intersect(donghua.genres.toSet()).size
                val sameStudio = if (other.studio == donghua.studio) 2 else 0
                commonGenres * 2 + sameStudio + (other.rating / 2.0).toInt()
            }
            .take(6)
    }

    // --- Watch History ---
    fun getWatchHistory(): Flow<List<WatchHistoryItem>> {
        return dao.getAllWatchHistory().map { list ->
            list.mapNotNull { entity ->
                val donghua = getDonghuaById(entity.donghuaId) ?: return@mapNotNull null
                val episode = donghua.episodes.find { it.id == entity.episodeId }
                    ?: donghua.episodes.firstOrNull() ?: return@mapNotNull null

                val totalDuration = if (entity.totalDurationSeconds > 0) entity.totalDurationSeconds else episode.durationSeconds
                val progress = if (totalDuration > 0) entity.playbackPositionSeconds.toFloat() / totalDuration else 0f

                WatchHistoryItem(
                    donghua = donghua,
                    episode = episode,
                    progressSeconds = entity.playbackPositionSeconds,
                    totalDurationSeconds = totalDuration,
                    progressPercentage = progress.coerceIn(0f, 1f),
                    lastWatchedTimestamp = entity.lastWatchedTimestamp
                )
            }
        }
    }

    suspend fun saveWatchProgress(
        donghuaId: String,
        episodeId: String,
        episodeNumber: Int,
        positionSeconds: Int,
        totalSeconds: Int
    ) {
        dao.saveWatchHistory(
            WatchHistoryEntity(
                donghuaId = donghuaId,
                episodeId = episodeId,
                episodeNumber = episodeNumber,
                playbackPositionSeconds = positionSeconds,
                totalDurationSeconds = totalSeconds,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() = dao.clearAllHistory()

    suspend fun deleteHistoryItem(donghuaId: String) = dao.deleteWatchHistory(donghuaId)

    // --- Favorites ---
    fun getFavoriteDonghua(): Flow<List<Donghua>> {
        return dao.getAllFavorites().map { list ->
            list.mapNotNull { getDonghuaById(it.donghuaId) }
        }
    }

    fun isFavorite(donghuaId: String): Flow<Boolean> = dao.isFavorite(donghuaId)

    suspend fun toggleFavorite(donghuaId: String, currentIsFavorite: Boolean) {
        if (currentIsFavorite) {
            dao.removeFavorite(donghuaId)
        } else {
            dao.addFavorite(FavoriteEntity(donghuaId = donghuaId))
        }
    }

    // --- Downloads & Offline Management ---
    fun getAllDownloads(): Flow<List<DownloadItem>> {
        return dao.getAllDownloads().map { list ->
            list.map { entity ->
                val quality = VideoQuality.fromLabel(entity.quality)
                val progress = if (entity.totalBytes > 0) entity.downloadedBytes.toFloat() / entity.totalBytes else 0f
                DownloadItem(
                    id = entity.id,
                    donghuaId = entity.donghuaId,
                    donghuaTitle = entity.donghuaTitle,
                    episodeId = entity.episodeId,
                    episodeNumber = entity.episodeNumber,
                    episodeTitle = entity.episodeTitle,
                    quality = quality,
                    totalBytes = entity.totalBytes,
                    downloadedBytes = entity.downloadedBytes,
                    progress = progress.coerceIn(0f, 1f),
                    status = try { DownloadStatus.valueOf(entity.status) } catch (e: Exception) { DownloadStatus.COMPLETED },
                    speedKbps = entity.speedKbps,
                    isMovie = entity.isMovie,
                    localFilePath = entity.localFilePath,
                    createdAt = entity.timestamp
                )
            }
        }
    }

    suspend fun startDownload(
        donghua: Donghua,
        episode: Episode,
        quality: VideoQuality
    ) {
        val downloadId = "${donghua.id}_${episode.id}_${quality.name}"
        val sizeMultiplier = when (quality) {
            VideoQuality.SD_360P -> 0.5
            VideoQuality.HD_720P -> 1.0
            VideoQuality.FHD_1080P -> 2.2
            VideoQuality.UHD_4K -> 5.5
        }
        val totalBytes = (episode.downloadSizeMb * 1024L * 1024L * sizeMultiplier).toLong()

        val initialEntity = DownloadEntity(
            id = downloadId,
            donghuaId = donghua.id,
            donghuaTitle = donghua.title,
            episodeId = episode.id,
            episodeNumber = episode.episodeNumber,
            episodeTitle = episode.title,
            quality = quality.label,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            status = DownloadStatus.DOWNLOADING.name,
            speedKbps = 2400,
            isMovie = donghua.isMovie,
            localFilePath = "/storage/emulated/0/DonghuaStream/Downloads/${donghua.id}_ep${episode.episodeNumber}_${quality.name}.mp4"
        )
        dao.insertOrUpdateDownload(initialEntity)

        // Cancel previous job if any
        activeDownloadJobs[downloadId]?.cancel()

        // Start progressive download simulation in background
        val job = scope.launch {
            val steps = 20
            var downloaded = 0L
            val stepBytes = totalBytes / steps

            for (i in 1..steps) {
                delay(600)
                downloaded = (stepBytes * i).coerceAtMost(totalBytes)
                val currentSpeed = (2000..3800).random()
                val isDone = i == steps
                val status = if (isDone) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING

                dao.updateDownloadProgress(
                    id = downloadId,
                    downloaded = downloaded,
                    status = status.name,
                    speed = if (isDone) 0 else currentSpeed
                )

                val progress = downloaded.toFloat() / totalBytes
                _downloadProgressMap.value = _downloadProgressMap.value + (downloadId to progress)
            }
            activeDownloadJobs.remove(downloadId)
        }
        activeDownloadJobs[downloadId] = job
    }

    suspend fun pauseDownload(downloadId: String) {
        activeDownloadJobs[downloadId]?.cancel()
        activeDownloadJobs.remove(downloadId)
        dao.updateDownloadProgress(downloadId, 0L, DownloadStatus.PAUSED.name, 0)
    }

    suspend fun deleteDownload(downloadId: String) {
        activeDownloadJobs[downloadId]?.cancel()
        activeDownloadJobs.remove(downloadId)
        _downloadProgressMap.value = _downloadProgressMap.value - downloadId
        dao.deleteDownload(downloadId)
    }

    suspend fun clearAllDownloads() {
        activeDownloadJobs.values.forEach { it.cancel() }
        activeDownloadJobs.clear()
        _downloadProgressMap.value = emptyMap()
        dao.clearAllDownloads()
    }

    // --- VIP Subscription ---
    fun getUserSubscription(): Flow<UserSubscription> {
        return dao.getUserSubscription().map { entity ->
            if (entity == null || !entity.isVip) {
                UserSubscription(isVip = false)
            } else {
                val now = System.currentTimeMillis()
                val isStillValid = entity.expiryDate > now
                val remainingDays = if (isStillValid) {
                    (((entity.expiryDate - now) / (1000 * 60 * 60 * 24)).toInt() + 1).coerceAtLeast(1)
                } else 0

                UserSubscription(
                    isVip = isStillValid,
                    planId = entity.planId,
                    planName = entity.planName,
                    startDate = entity.startDate,
                    expiryDate = entity.expiryDate,
                    remainingDays = remainingDays,
                    paymentMethod = entity.paymentMethod
                )
            }
        }
    }

    suspend fun subscribeVip(plan: VipPlan, paymentMethod: PaymentMethod) {
        val now = System.currentTimeMillis()
        val durationMillis = plan.durationDays * 24L * 60L * 60L * 1000L
        val expiry = now + durationMillis

        val entity = UserSubscriptionEntity(
            id = 1,
            isVip = true,
            planId = plan.id,
            planName = plan.name,
            startDate = now,
            expiryDate = expiry,
            paymentMethod = paymentMethod.title
        )
        dao.saveSubscription(entity)
    }

    suspend fun cancelSubscription() {
        val entity = UserSubscriptionEntity(
            id = 1,
            isVip = false,
            planId = "",
            planName = "",
            startDate = 0L,
            expiryDate = 0L,
            paymentMethod = ""
        )
        dao.saveSubscription(entity)
    }
}
