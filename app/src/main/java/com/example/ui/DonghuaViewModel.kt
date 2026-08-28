package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DonghuaDatabase
import com.example.data.model.AuthMode
import com.example.data.model.Donghua
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.Episode
import com.example.data.model.PaymentMethod
import com.example.data.model.UserAccount
import com.example.data.model.UserSubscription
import com.example.data.model.VideoQuality
import com.example.data.model.VipPlan
import com.example.data.model.WatchHistoryItem
import com.example.data.repository.DonghuaCatalog
import com.example.data.repository.DonghuaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object History : Screen()
    object Schedule : Screen()
    object Explore : Screen()
    data class Detail(val donghuaId: String) : Screen()
    data class Player(val donghuaId: String, val episodeId: String, val isOffline: Boolean = false) : Screen()
    object Downloads : Screen()
    object Profile : Screen()
}

class DonghuaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DonghuaDatabase.getDatabase(application)
    val repository = DonghuaRepository(database.donghuaDao(), viewModelScope)

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("Semua")
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _selectedStudio = MutableStateFlow("Semua Studio")
    val selectedStudio = _selectedStudio.asStateFlow()

    private val _selectedUploadDay = MutableStateFlow("Semua Hari")
    val selectedUploadDay = _selectedUploadDay.asStateFlow()

    // Dynamic Catalog Flow
    val allDonghua: StateFlow<List<Donghua>> = repository.dynamicCatalog

    // Notification toast banner (for instant episode releases, logins, etc.)
    private val _bannerNotification = MutableStateFlow<String?>(null)
    val bannerNotification = _bannerNotification.asStateFlow()

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow(
        UserAccount(
            id = "user_guest",
            email = "tamu@donghua.id",
            name = "Kultivator Donghua",
            isLoggedIn = false,
            exp = 12500, // Default 12.500 EXP (Qualifies for 20% discount!)
            isOwner = false
        )
    )
    val currentUser = _currentUser.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog = _showAuthDialog.asStateFlow()

    private val _authMode = MutableStateFlow(com.example.data.model.AuthMode.LOGIN)
    val authMode = _authMode.asStateFlow()

    private val _authEmail = MutableStateFlow("")
    val authEmail = _authEmail.asStateFlow()

    private val _authPassword = MutableStateFlow("")
    val authPassword = _authPassword.asStateFlow()

    private val _authName = MutableStateFlow("")
    val authName = _authName.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage = _authErrorMessage.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = _authSuccessMessage.asStateFlow()

    private val _isResetOtpSent = MutableStateFlow(false)
    val isResetOtpSent = _isResetOtpSent.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating = _isAuthenticating.asStateFlow()

    // Subscribed VIP State
    val userSubscription: StateFlow<UserSubscription> = repository.getUserSubscription()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSubscription())

    // Watch History
    val watchHistory: StateFlow<List<WatchHistoryItem>> = repository.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites
    val favoriteDonghua: StateFlow<List<Donghua>> = repository.getFavoriteDonghua()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Downloads
    val downloads: StateFlow<List<DownloadItem>> = repository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Smart Personalized Recommendations
    val recommendations: StateFlow<List<Donghua>> = repository.getPersonalizedRecommendations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected items
    private val _selectedDonghua = MutableStateFlow<Donghua?>(null)
    val selectedDonghua = _selectedDonghua.asStateFlow()

    private val _selectedEpisode = MutableStateFlow<Episode?>(null)
    val selectedEpisode = _selectedEpisode.asStateFlow()

    // Video Player State
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0)
    val playbackPosition = _playbackPosition.asStateFlow()

    private val _playbackDuration = MutableStateFlow(1200)
    val playbackDuration = _playbackDuration.asStateFlow()

    private val _selectedQuality = MutableStateFlow(VideoQuality.HD_720P)
    val selectedQuality = _selectedQuality.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _selectedSubtitle = MutableStateFlow("Indonesia")
    val selectedSubtitle = _selectedSubtitle.asStateFlow()

    private val _showAdOverlay = MutableStateFlow(false)
    val showAdOverlay = _showAdOverlay.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown = _adCountdown.asStateFlow()

    // VIP Dialog State
    private val _showVipDialog = MutableStateFlow(false)
    val showVipDialog = _showVipDialog.asStateFlow()

    private val _selectedVipPlan = MutableStateFlow(DonghuaCatalog.VIP_PLANS[2]) // Default 30 hari (Rp 20.000)
    val selectedVipPlan = _selectedVipPlan.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.GOPAY)
    val selectedPaymentMethod = _selectedPaymentMethod.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment = _isProcessingPayment.asStateFlow()

    private val _paymentSuccessMessage = MutableStateFlow<String?>(null)
    val paymentSuccessMessage = _paymentSuccessMessage.asStateFlow()

    // Download modal state in detail/player
    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog = _showDownloadDialog.asStateFlow()

    private var playerTickerJob: Job? = null
    private var adTickerJob: Job? = null

    init {
        // Check if current user is owner
        checkOwnerPrivilege(_currentUser.value)
    }

    private fun checkOwnerPrivilege(user: UserAccount) {
        if (user.email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)) {
            val ownerAccount = user.copy(
                name = UserAccount.OWNER_NAME + " 👑 (Owner)",
                isOwner = true,
                isLoggedIn = true,
                exp = 100_000 // Max EXP for owner
            )
            _currentUser.value = ownerAccount
            // Automatically grant lifetime VIP for owner
            viewModelScope.launch {
                repository.subscribeVip(
                    VipPlan(
                        id = "owner_vip",
                        name = "👑 Owner Lifetime VIP",
                        durationDays = 99999,
                        priceRupiah = 0,
                        priceFormatted = "Gratis (Owner)",
                        pricePerDay = "Rp 0",
                        description = "Akses penuh seumur hidup bebas iklan, 4K UHD, dan rilis tercepat untuk Owner."
                    ),
                    PaymentMethod.GOPAY
                )
            }
        }
    }

    // EXP Management
    fun addExp(amount: Int, reason: String = "Aktivitas Donghua") {
        val current = _currentUser.value
        val newExp = (current.exp + amount).coerceAtMost(100_000)
        _currentUser.value = current.copy(exp = newExp)
        val discount = UserAccount.getExpDiscountPercent(newExp)
        _bannerNotification.value = "+$amount EXP didapatkan ($reason)! Total EXP: $newExp (${UserAccount.getCultivationRank(newExp)}) • Diskon VIP $discount%"
    }

    fun setExp(amount: Int) {
        val clamped = amount.coerceIn(0, 100_000)
        val current = _currentUser.value
        _currentUser.value = current.copy(exp = clamped)
        val discount = UserAccount.getExpDiscountPercent(clamped)
        _bannerNotification.value = "EXP diatur ke $clamped! Diskon VIP saat ini: $discount%"
    }

    fun loginAsOwner() {
        _currentUser.value = UserAccount(
            id = "owner_sabdha",
            email = UserAccount.OWNER_EMAIL,
            name = UserAccount.OWNER_NAME + " 👑 (Owner)",
            isGoogleUser = true,
            isLoggedIn = true,
            exp = 100_000,
            isOwner = true
        )
        checkOwnerPrivilege(_currentUser.value)
        _bannerNotification.value = "👑 Selamat datang Owner Sabdha Prasetya Suroso! Akses VIP Seumur Hidup & Bebas Semua Fitur aktif."
        closeAuthDialog()
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        if (screen is Screen.Detail) {
            _selectedDonghua.value = repository.getDonghuaById(screen.donghuaId)
        } else if (screen is Screen.Player) {
            val donghua = repository.getDonghuaById(screen.donghuaId)
            _selectedDonghua.value = donghua
            val episode = donghua?.episodes?.find { it.id == screen.episodeId } ?: donghua?.episodes?.firstOrNull()
            _selectedEpisode.value = episode
            startPlayerSession(donghua, episode, screen.isOffline)
        }
        _currentScreen.value = screen
    }

    fun goBack() {
        when (_currentScreen.value) {
            is Screen.Player -> {
                stopPlayerSession()
                val donghua = _selectedDonghua.value
                if (donghua != null) {
                    _currentScreen.value = Screen.Detail(donghua.id)
                } else {
                    _currentScreen.value = Screen.Home
                }
            }
            is Screen.Detail -> _currentScreen.value = Screen.Home
            Screen.History, Screen.Schedule, Screen.Explore, Screen.Downloads, Screen.Profile -> _currentScreen.value = Screen.Home
            Screen.Home -> {}
        }
    }

    // Avatar Management
    fun setUserCustomAvatar(uriString: String) {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            avatarUrl = uriString,
            avatarPresetId = null
        )
        _bannerNotification.value = "Foto profil kustom dari galeri berhasil diterapkan!"
    }

    fun setUserPresetAvatar(presetId: String) {
        val current = _currentUser.value
        val preset = com.example.data.model.UserAccount.BUILTIN_AVATARS.find { it.id == presetId }
        val name = preset?.characterName ?: presetId
        _currentUser.value = current.copy(
            avatarUrl = null,
            avatarPresetId = presetId
        )
        _bannerNotification.value = "Avatar kultivator '$name' berhasil dipasang!"
    }

    // Watch History Management
    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _bannerNotification.value = "Riwayat tontonan berhasil dibersihkan."
        }
    }

    fun deleteHistoryItem(donghuaId: String) {
        viewModelScope.launch {
            repository.deleteHistoryItem(donghuaId)
            _bannerNotification.value = "Item dihapus dari riwayat tontonan."
        }
    }

    // Search and filter
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun setSelectedStudio(studio: String) {
        _selectedStudio.value = studio
    }

    fun setSelectedUploadDay(day: String) {
        _selectedUploadDay.value = day
    }

    fun getFilteredCatalog(): List<Donghua> {
        return repository.searchDonghua(
            query = _searchQuery.value,
            selectedGenre = _selectedGenre.value,
            selectedStudio = _selectedStudio.value,
            selectedDay = _selectedUploadDay.value
        )
    }

    fun getDonghuaByDay(day: String): List<Donghua> {
        return repository.getDonghuaByDay(day)
    }

    fun getTodayUploads(): List<Donghua> {
        return repository.getTodayUploads()
    }

    // Instant Release Simulation
    fun triggerInstantRelease(donghuaId: String) {
        viewModelScope.launch {
            val updated = repository.releaseNewEpisodeInstantly(donghuaId)
            if (updated != null) {
                val epNumber = updated.currentEpisodes
                _bannerNotification.value = "🔥 Rilis Baru! ${updated.title} Episode $epNumber baru saja diupload dan siap ditonton!"
                // If currently viewing this donghua detail, update selectedDonghua
                if (_selectedDonghua.value?.id == donghuaId) {
                    _selectedDonghua.value = updated
                }
            }
        }
    }

    fun dismissBannerNotification() {
        _bannerNotification.value = null
    }

    // --- Authentication Operations ---
    fun openAuthDialog(mode: com.example.data.model.AuthMode = com.example.data.model.AuthMode.LOGIN) {
        _authMode.value = mode
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
        _isResetOtpSent.value = false
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    fun setAuthMode(mode: com.example.data.model.AuthMode) {
        _authMode.value = mode
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    fun setAuthEmail(email: String) {
        _authEmail.value = email
    }

    fun setAuthPassword(password: String) {
        _authPassword.value = password
    }

    fun setAuthName(name: String) {
        _authName.value = name
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun loginWithEmail() {
        val email = _authEmail.value.trim()
        val password = _authPassword.value
        if (email.isBlank() || !email.contains("@")) {
            _authErrorMessage.value = "Masukkan alamat email yang valid!"
            return
        }
        if (password.length < 6) {
            _authErrorMessage.value = "Password minimal 6 karakter!"
            return
        }

        viewModelScope.launch {
            _isAuthenticating.value = true
            _authErrorMessage.value = null
            delay(800)
            val username = if (email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)) {
                UserAccount.OWNER_NAME + " 👑 (Owner)"
            } else {
                email.substringBefore("@").replaceFirstChar { it.uppercase() }
            }
            val account = UserAccount(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                name = username,
                isGoogleUser = false,
                isLoggedIn = true,
                exp = if (email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)) 100_000 else 12500,
                isOwner = email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)
            )
            _currentUser.value = account
            checkOwnerPrivilege(account)
            _isAuthenticating.value = false
            _bannerNotification.value = "Selamat datang kembali, $username! Anda telah berhasil masuk."
            closeAuthDialog()
        }
    }

    fun registerWithEmail() {
        val name = _authName.value.trim()
        val email = _authEmail.value.trim()
        val password = _authPassword.value

        if (name.isBlank()) {
            _authErrorMessage.value = "Nama lengkap tidak boleh kosong!"
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _authErrorMessage.value = "Masukkan alamat email yang valid!"
            return
        }
        if (password.length < 6) {
            _authErrorMessage.value = "Password minimal 6 karakter!"
            return
        }

        viewModelScope.launch {
            _isAuthenticating.value = true
            _authErrorMessage.value = null
            delay(900)
            val isOwner = email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)
            val account = UserAccount(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                name = if (isOwner) UserAccount.OWNER_NAME + " 👑 (Owner)" else name,
                isGoogleUser = false,
                isLoggedIn = true,
                exp = if (isOwner) 100_000 else 12500,
                isOwner = isOwner
            )
            _currentUser.value = account
            checkOwnerPrivilege(account)
            _isAuthenticating.value = false
            _bannerNotification.value = "Pendaftaran berhasil! Selamat datang di Donghua Stream, ${account.name}."
            closeAuthDialog()
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authErrorMessage.value = null
            delay(1000) // Simulate Google Identity Services / Credential Manager
            val email = "sabdhaprasetya@gmail.com" // Default to owner email or standard google login
            val isOwner = email.equals(UserAccount.OWNER_EMAIL, ignoreCase = true)
            val account = UserAccount(
                id = "google_user_99812",
                email = email,
                name = if (isOwner) UserAccount.OWNER_NAME + " 👑 (Owner)" else "Kultivator Google",
                avatarUrl = "https://lh3.googleusercontent.com/a/default-user",
                isGoogleUser = true,
                isLoggedIn = true,
                exp = if (isOwner) 100_000 else 12500,
                isOwner = isOwner
            )
            _currentUser.value = account
            checkOwnerPrivilege(account)
            _isAuthenticating.value = false
            _bannerNotification.value = "Berhasil masuk dengan Akun Google ($email)!"
            closeAuthDialog()
        }
    }

    fun sendPasswordResetEmail() {
        val email = _authEmail.value.trim()
        if (email.isBlank() || !email.contains("@")) {
            _authErrorMessage.value = "Masukkan alamat email akun Anda yang terdaftar!"
            return
        }

        viewModelScope.launch {
            _isAuthenticating.value = true
            _authErrorMessage.value = null
            delay(1000)
            _isResetOtpSent.value = true
            _isAuthenticating.value = false
            _authSuccessMessage.value = "Tautan & Kode OTP reset password telah dikirimkan ke $email. Silakan periksa kotak masuk atau spam email Anda."
        }
    }

    fun logout() {
        _currentUser.value = com.example.data.model.UserAccount(isLoggedIn = false)
        _bannerNotification.value = "Anda telah berhasil keluar dari akun."
    }

    // Favorites
    fun toggleFavorite(donghuaId: String, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(donghuaId, currentIsFavorite)
        }
    }

    // Video Player
    private fun startPlayerSession(donghua: Donghua?, episode: Episode?, isOffline: Boolean) {
        playerTickerJob?.cancel()
        adTickerJob?.cancel()

        _playbackPosition.value = 0
        _playbackDuration.value = episode?.durationSeconds ?: 1200
        _isPlaying.value = true

        val isVipOrOwner = userSubscription.value.isVip || _currentUser.value.isOwner
        if (!isVipOrOwner && !isOffline) {
            // Non-VIP user gets exactly 1 pre-roll ad overlay per episode (5s)
            _showAdOverlay.value = true
            _adCountdown.value = 5
            adTickerJob = viewModelScope.launch {
                for (i in 5 downTo 1) {
                    _adCountdown.value = i
                    delay(1000)
                }
                _showAdOverlay.value = false
            }
        } else {
            _showAdOverlay.value = false
        }

        // Set default quality based on VIP/Owner
        _selectedQuality.value = if (isVipOrOwner) VideoQuality.UHD_4K else VideoQuality.HD_720P

        // Award +150 EXP for watching an episode organically
        addExp(150, "Menonton ${donghua?.title ?: "Donghua"}")

        // Start playback ticker
        playerTickerJob = viewModelScope.launch {
            var watchSecondsAcc = 0
            while (true) {
                delay(1000)
                if (!isActive) break
                if (_isPlaying.value && !_showAdOverlay.value) {
                    _playbackPosition.value += (1 * _playbackSpeed.value).toInt()
                    watchSecondsAcc += 1

                    // Earn incremental EXP organically every 30 seconds of active watching (+50 EXP)
                    if (watchSecondsAcc % 30 == 0) {
                        addExp(50, "Durasi Menonton Aktif")
                    }

                    if (_playbackPosition.value >= _playbackDuration.value) {
                        _playbackPosition.value = _playbackDuration.value
                        _isPlaying.value = false
                        // Bonus completion EXP
                        addExp(100, "Menyelesaikan Episode Penuh")
                    }

                    // Save watch progress every 5 seconds
                    if (_playbackPosition.value % 5 == 0 && donghua != null && episode != null) {
                        repository.saveWatchProgress(
                            donghuaId = donghua.id,
                            episodeId = episode.id,
                            episodeNumber = episode.episodeNumber,
                            positionSeconds = _playbackPosition.value,
                            totalSeconds = _playbackDuration.value
                        )
                    }
                }
            }
        }
    }

    private fun stopPlayerSession() {
        playerTickerJob?.cancel()
        adTickerJob?.cancel()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(seconds: Int) {
        _playbackPosition.value = seconds.coerceIn(0, _playbackDuration.value)
    }

    fun seekRelative(secondsDelta: Int) {
        seekTo(_playbackPosition.value + secondsDelta)
    }

    fun setQuality(quality: VideoQuality) {
        val isVipOrOwner = userSubscription.value.isVip || _currentUser.value.isOwner
        if (quality.requiresVip && !isVipOrOwner) {
            _showVipDialog.value = true
        } else {
            _selectedQuality.value = quality
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun setSubtitle(subtitle: String) {
        _selectedSubtitle.value = subtitle
    }

    fun skipAd() {
        adTickerJob?.cancel()
        _showAdOverlay.value = false
    }

    fun selectEpisode(episode: Episode) {
        val donghua = _selectedDonghua.value ?: return
        val isVipOrOwner = userSubscription.value.isVip || _currentUser.value.isOwner
        if (episode.isVipOnly && !isVipOrOwner) {
            _showVipDialog.value = true
            return
        }
        _selectedEpisode.value = episode
        startPlayerSession(donghua, episode, false)
    }

    // Downloads
    fun openDownloadDialog() {
        _showDownloadDialog.value = true
    }

    fun closeDownloadDialog() {
        _showDownloadDialog.value = false
    }

    fun downloadEpisode(donghua: Donghua, episode: Episode, quality: VideoQuality) {
        val isVipOrOwner = userSubscription.value.isVip || _currentUser.value.isOwner
        if (quality.requiresVip && !isVipOrOwner) {
            _showDownloadDialog.value = false
            _showVipDialog.value = true
            return
        }
        viewModelScope.launch {
            repository.startDownload(donghua, episode, quality)
            _showDownloadDialog.value = false
        }
    }

    fun pauseDownload(downloadId: String) {
        viewModelScope.launch {
            repository.pauseDownload(downloadId)
        }
    }

    fun resumeDownload(item: DownloadItem) {
        val donghua = repository.getDonghuaById(item.donghuaId) ?: return
        val episode = donghua.episodes.find { it.id == item.episodeId } ?: return
        downloadEpisode(donghua, episode, item.quality)
    }

    fun deleteDownload(downloadId: String) {
        viewModelScope.launch {
            repository.deleteDownload(downloadId)
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            repository.clearAllDownloads()
        }
    }

    // VIP Subscription
    fun openVipDialog(preselectedPlan: VipPlan? = null) {
        if (preselectedPlan != null) {
            _selectedVipPlan.value = preselectedPlan
        }
        _paymentSuccessMessage.value = null
        _showVipDialog.value = true
    }

    fun closeVipDialog() {
        _showVipDialog.value = false
        _paymentSuccessMessage.value = null
    }

    fun selectVipPlan(plan: VipPlan) {
        _selectedVipPlan.value = plan
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun processVipPayment() {
        viewModelScope.launch {
            _isProcessingPayment.value = true
            delay(1200) // Simulate GoPay instant verification
            val plan = _selectedVipPlan.value
            val method = _selectedPaymentMethod.value
            val discount = _currentUser.value.discountPercent
            val finalPriceFormatted = plan.getDiscountedPriceFormatted(discount)
            
            repository.subscribeVip(plan, method)
            _isProcessingPayment.value = false
            _paymentSuccessMessage.value = "Pembayaran GoPay atas nama Sabdha Prasetya Suroso (0895402865399) berhasil diverifikasi! Paket ${plan.name} ($finalPriceFormatted - Diskon EXP $discount%) telah aktif."
            // Remove ad overlay if active
            _showAdOverlay.value = false
            _selectedQuality.value = VideoQuality.UHD_4K
            // Grant organic bonus +10,000 EXP on VIP subscription purchase
            addExp(10000, "Bonus Berlangganan VIP")
        }
    }

    fun cancelVip() {
        viewModelScope.launch {
            repository.cancelSubscription()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayerSession()
    }
}
