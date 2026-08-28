package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

enum class VideoQuality(val label: String, val resolution: String, val bitRate: String, val requiresVip: Boolean) {
    SD_360P("360p", "640x360", "800 kbps", false),
    HD_720P("720p HD", "1280x720", "2.5 Mbps", false),
    FHD_1080P("1080p FHD", "1920x1080", "5.0 Mbps", false),
    UHD_4K("4K Ultra HD", "3840x2160", "15.0 Mbps", true);

    companion object {
        fun fromLabel(label: String): VideoQuality {
            return entries.find { it.label.contains(label, ignoreCase = true) || it.name == label } ?: HD_720P
        }
    }
}

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class Episode(
    val id: String,
    val donghuaId: String,
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val durationSeconds: Int,
    val downloadSizeMb: Int,
    val isVipOnly: Boolean = false,
    val videoUrl: String = "",
    val synopsis: String = "",
    val isNewlyReleased: Boolean = false,
    val releaseDateText: String = "Hari ini",
    val seasonName: String = "Season 1",
    val seasonNumber: Int = 1,
    val globalEpisodeNumber: Int = episodeNumber
)

data class Donghua(
    val id: String,
    val title: String,
    val chineseTitle: String,
    val studio: String,
    val genres: List<String>,
    val synopsis: String,
    val rating: Double,
    val totalEpisodes: Int,
    val currentEpisodes: Int,
    val status: String, // "Ongoing" or "Tamat"
    val releaseYear: Int,
    val isMovie: Boolean = false,
    val isVipExclusive: Boolean = false,
    val viewCount: String,
    val bannerDrawableRes: Int? = null,
    val posterDrawableRes: Int? = null,
    val posterUrl: String? = null,
    val badgeText: String? = "New",
    val badgeColorHex: Long = 0xFF7C4DFF,
    val posterColorHex: Long = 0xFF1E293B,
    val episodes: List<Episode> = emptyList(),
    val seasons: List<String> = emptyList(),
    val durationMinutes: Int? = null,
    val uploadDay: String = "Senin", // "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
    val uploadTime: String = "10:00 WIB",
    val isRecentlyUpdated: Boolean = false,
    val latestEpisodeUpdateNote: String = ""
)

data class AvatarPreset(
    val id: String,
    val characterName: String,
    val donghuaTitle: String,
    val title: String,
    val emoji: String,
    val gradientColors: List<Long>,
    val accentColor: Long
)

data class UserAccount(
    val id: String = "user_guest",
    val email: String = "tamu@donghua.id",
    val name: String = "Kultivator Donghua",
    val avatarUrl: String? = null,
    val avatarPresetId: String? = "xiao_yan",
    val isGoogleUser: Boolean = false,
    val isLoggedIn: Boolean = false,
    val exp: Int = 12500, // Starting EXP for culturing & discount rewards
    val isOwner: Boolean = false
) {
    val discountPercent: Int
        get() = getExpDiscountPercent(exp)

    val cultivationRank: String
        get() = getCultivationRank(exp)

    companion object {
        const val OWNER_EMAIL = "sabdhaprasetya@gmail.com"
        const val OWNER_NAME = "Sabdha Prasetya Suroso"
        const val OWNER_GOPAY_NUMBER = "0895402865399"

        val BUILTIN_AVATARS = listOf(
            AvatarPreset("xiao_yan", "Xiao Yan", "Battle Through the Heavens", "Kaisar Api • Teratai Surgawi", "🔥", listOf(0xFFBF360C, 0xFFE65100), 0xFFFF9800),
            AvatarPreset("shi_hao", "Shi Hao", "Perfect World", "Kaisar Huang • Primordial", "👑", listOf(0xFFB78103, 0xFF6D4C41), 0xFFFFD700),
            AvatarPreset("wang_lin", "Wang Lin", "Renegade Immortal", "Dewa Pembunuh • Xian Ni", "⚔️", listOf(0xFF212121, 0xFF4A148C), 0xFFE040FB),
            AvatarPreset("tang_san", "Tang San", "Soul Land", "Dewa Laut & Asura", "🌊", listOf(0xFF01579B, 0xFF00838F), 0xFF40C4FF),
            AvatarPreset("medusa", "Ratu Medusa", "Battle Through the Heavens", "Ratu Ular Sembilan Warna", "🐍", listOf(0xFF880E4F, 0xFF4A148C), 0xFFFF4081),
            AvatarPreset("yun_yun", "Yun Yun", "Battle Through the Heavens", "Master Sekte Awan", "🌪️", listOf(0xFF004D40, 0xFF00796B), 0xFF64FFDA),
            AvatarPreset("lin_dong", "Lin Dong", "Martial Universe", "Leluhur Bela Diri", "⚡", listOf(0xFF311B92, 0xFF1A237E), 0xFF7C4DFF),
            AvatarPreset("luo_feng", "Luo Feng", "Swallowed Star", "Tuan Planet Bumi", "🌌", listOf(0xFF006064, 0xFF0D47A1), 0xFF00E5FF),
            AvatarPreset("xiao_wu", "Xiao Wu", "Soul Land", "Kelinci Tulang Lunak", "🌸", listOf(0xFF880E4F, 0xFFAD1457), 0xFFFF80AB),
            AvatarPreset("ye_fan", "Ye Fan", "Shrouding the Heavens", "Tubuh Suci Purba", "🗡️", listOf(0xFF3E2723, 0xFFBF360C), 0xFFFF6E40)
        )

        fun getExpDiscountPercent(exp: Int): Int {
            return when {
                exp >= 100_000 -> 30 // Max 100k XP: 30% discount
                exp >= 20_000 -> 20  // 20k+ XP: 20% discount
                exp >= 10_000 -> 20  // 10k+ XP: 20% discount
                else -> 0
            }
        }

        fun getCultivationRank(exp: Int): String {
            return when {
                exp >= 100_000 -> "Kaisar Abadi (Immortal Emperor)"
                exp >= 50_000 -> "Jiwa Murni (Nascent Soul)"
                exp >= 20_000 -> "Inti Emas (Golden Core)"
                exp >= 10_000 -> "Pembentukan Pondasi (Foundation)"
                else -> "Kondensasi Qi (Qi Condensation)"
            }
        }
    }
}

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

data class VipPlan(
    val id: String,
    val name: String,
    val durationDays: Int,
    val priceRupiah: Int,
    val priceFormatted: String,
    val pricePerDay: String,
    val isPopular: Boolean = false,
    val discountBadge: String? = null,
    val description: String
) {
    fun getDiscountedPriceRupiah(discountPercent: Int): Int {
        if (discountPercent <= 0) return priceRupiah
        val discountAmount = (priceRupiah * discountPercent) / 100
        return priceRupiah - discountAmount
    }

    fun getDiscountedPriceFormatted(discountPercent: Int): String {
        val discounted = getDiscountedPriceRupiah(discountPercent)
        val formatted = java.text.NumberFormat.getIntegerInstance(java.util.Locale("in", "ID")).format(discounted)
        return "Rp $formatted"
    }
}

enum class PaymentMethod(
    val id: String,
    val title: String,
    val category: String,
    val accountNumber: String = "0895402865399",
    val accountName: String = "Sabdha Prasetya Suroso"
) {
    GOPAY("gopay", "GoPay Official", "E-Wallet (Tersedia)", "0895402865399", "Sabdha Prasetya Suroso");

    companion object {
        const val GOPAY_ACCOUNT_NAME = "Sabdha Prasetya Suroso"
        const val GOPAY_NUMBER = "0895402865399"
    }
}

data class UserSubscription(
    val isVip: Boolean = false,
    val planId: String = "",
    val planName: String = "",
    val startDate: Long = 0L,
    val expiryDate: Long = 0L,
    val remainingDays: Int = 0,
    val paymentMethod: String = ""
)

data class DownloadItem(
    val id: String,
    val donghuaId: String,
    val donghuaTitle: String,
    val episodeId: String,
    val episodeNumber: Int,
    val episodeTitle: String,
    val quality: VideoQuality,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val progress: Float,
    val status: DownloadStatus,
    val speedKbps: Int,
    val isMovie: Boolean = false,
    val localFilePath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class WatchHistoryItem(
    val donghua: Donghua,
    val episode: Episode,
    val progressSeconds: Int,
    val totalDurationSeconds: Int,
    val progressPercentage: Float,
    val lastWatchedTimestamp: Long
)
