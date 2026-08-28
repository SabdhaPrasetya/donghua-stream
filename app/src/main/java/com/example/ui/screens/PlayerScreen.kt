package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Episode
import com.example.data.model.VideoQuality
import com.example.ui.DonghuaViewModel
import com.example.ui.Screen
import com.example.ui.components.AdOverlay
import com.example.ui.components.DownloadEpisodeDialog
import com.example.ui.components.VipBadge
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LotusPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun PlayerScreen(
    donghuaId: String,
    episodeId: String,
    isOffline: Boolean = false,
    viewModel: DonghuaViewModel,
    modifier: Modifier = Modifier
) {
    val donghua = viewModel.repository.getDonghuaById(donghuaId)
    val subscription by viewModel.userSubscription.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val playbackDuration by viewModel.playbackDuration.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val selectedSubtitle by viewModel.selectedSubtitle.collectAsState()
    val showAdOverlay by viewModel.showAdOverlay.collectAsState()
    val adCountdown by viewModel.adCountdown.collectAsState()
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showSubtitleMenu by remember { mutableStateOf(false) }

    val episode = donghua?.episodes?.find { it.id == episodeId } ?: donghua?.episodes?.firstOrNull()

    if (donghua == null || episode == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Text(text = "Video tidak ditemukan", color = TextPrimary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- 16:9 Video Canvas / Player Area ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
                .testTag("video_player_box")
        ) {
            // Simulated Active Cinematic Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw deep fantasy gradient
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(donghua.posterColorHex).copy(alpha = 0.45f),
                            Color(0xFF070A12)
                        ),
                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                        radius = canvasWidth * 0.7f
                    )
                )

                // Spirit energy particles effect
                val time = playbackPosition.toFloat()
                for (i in 0..12) {
                    val x = (canvasWidth * ((i * 37 + time * 20) % canvasWidth) / canvasWidth)
                    val y = canvasHeight * 0.5f + sin(time + i) * 60f
                    drawCircle(
                        color = GoldPrimary.copy(alpha = 0.3f),
                        radius = (i % 4 + 2).toFloat() * 2f,
                        center = Offset(x, y)
                    )
                }
            }

            // Central watermark calligraphy & Title
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = donghua.chineseTitle,
                    color = Color.White.copy(alpha = 0.25f),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${donghua.title} - ${episode.title}",
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (isOffline) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CyanSecondary.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "MODE OFFLINE (Memutar File Lokal)",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Subtitle Display at Bottom
            if (selectedSubtitle != "Nonaktif") {
                val currentSubText = getDynamicSubtitle(
                    donghuaId = donghua.id,
                    positionSec = playbackPosition,
                    subtitleLang = selectedSubtitle
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp)
                        .padding(bottom = if (showControls) 56.dp else 14.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentSubText,
                            color = Color(0xFFFFF066),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black,
                                    offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                    blurRadius = 3f
                                )
                            )
                        )
                    }
                }
            }

            // Ad Overlay for Non-VIP
            if (showAdOverlay) {
                AdOverlay(
                    countdown = adCountdown,
                    onSkipClick = { viewModel.skipAd() },
                    onUpgradeVipClick = {
                        viewModel.skipAd()
                        viewModel.openVipDialog()
                    }
                )
            }

            // --- Player Overlay Controls ---
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls && !showAdOverlay,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    // Top Bar Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.goBack() },
                                modifier = Modifier.size(32.dp).testTag("player_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = donghua.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = episode.title,
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (subscription.isVip) {
                                VipBadge(text = "4K VIP", isCompact = true)
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GoldPrimary,
                                    modifier = Modifier.clickable { viewModel.openVipDialog() }
                                ) {
                                    Text(
                                        text = "VIP 4K",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Center Play/Pause & Seek 10s
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.seekRelative(-10) },
                            modifier = Modifier.size(40.dp).testTag("seek_backward_10s")
                        ) {
                            Icon(imageVector = Icons.Default.Replay10, contentDescription = "Mundur 10s", tint = TextPrimary, modifier = Modifier.size(32.dp))
                        }

                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary,
                            modifier = Modifier
                                .size(54.dp)
                                .clickable { viewModel.togglePlayPause() }
                                .testTag("play_pause_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.seekRelative(10) },
                            modifier = Modifier.size(40.dp).testTag("seek_forward_10s")
                        ) {
                            Icon(imageVector = Icons.Default.Forward10, contentDescription = "Maju 10s", tint = TextPrimary, modifier = Modifier.size(32.dp))
                        }
                    }

                    // Bottom Bar Controls & Scrubber
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        // Slider Scrubber
                        Slider(
                            value = playbackPosition.toFloat(),
                            onValueChange = { viewModel.seekTo(it.toInt()) },
                            valueRange = 0f..playbackDuration.toFloat().coerceAtLeast(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = GoldPrimary,
                                activeTrackColor = GoldPrimary,
                                inactiveTrackColor = TextSecondary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth().height(20.dp).testTag("player_scrubber")
                        )

                        // Bottom Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val posMin = playbackPosition / 60
                            val posSec = playbackPosition % 60
                            val durMin = playbackDuration / 60
                            val durSec = playbackDuration % 60
                            Text(
                                text = String.format("%02d:%02d / %02d:%02d", posMin, posSec, durMin, durSec),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Quality Selector
                                Box {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (selectedQuality == VideoQuality.UHD_4K) GoldPrimary else DarkSurfaceElevated,
                                        modifier = Modifier.clickable { showQualityMenu = true }.testTag("quality_selector_btn")
                                    ) {
                                        Text(
                                            text = selectedQuality.label,
                                            color = if (selectedQuality == VideoQuality.UHD_4K) Color.Black else TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showQualityMenu,
                                        onDismissRequest = { showQualityMenu = false }
                                    ) {
                                        VideoQuality.entries.forEach { quality ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(text = quality.label, fontWeight = FontWeight.Bold)
                                                        if (quality.requiresVip) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            VipBadge(text = "VIP", isCompact = true)
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.setQuality(quality)
                                                    showQualityMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Speed Selector
                                Box {
                                    IconButton(onClick = { showSpeedMenu = true }, modifier = Modifier.size(28.dp)) {
                                        Text(text = "${playbackSpeed}x", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    DropdownMenu(
                                        expanded = showSpeedMenu,
                                        onDismissRequest = { showSpeedMenu = false }
                                    ) {
                                        listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                            DropdownMenuItem(
                                                text = { Text(text = "${speed}x") },
                                                onClick = {
                                                    viewModel.setPlaybackSpeed(speed)
                                                    showSpeedMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Subtitles
                                Box {
                                    IconButton(onClick = { showSubtitleMenu = true }, modifier = Modifier.size(28.dp)) {
                                        Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = "Subtitles", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    DropdownMenu(
                                        expanded = showSubtitleMenu,
                                        onDismissRequest = { showSubtitleMenu = false }
                                    ) {
                                        listOf("Indonesia", "English", "Hanzi (中文)", "Nonaktif").forEach { sub ->
                                            DropdownMenuItem(
                                                text = { Text(text = sub) },
                                                onClick = {
                                                    viewModel.setSubtitle(sub)
                                                    showSubtitleMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Download
                                IconButton(
                                    onClick = { viewModel.openDownloadDialog() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Download, contentDescription = "Unduh", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Below Video: Details & Fast Episode Switcher ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = donghua.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${episode.title} • ${donghua.studio}",
                                color = GoldPrimary,
                                fontSize = 12.sp
                            )
                        }

                        if (!subscription.isVip) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldPrimary,
                                modifier = Modifier.clickable { viewModel.openVipDialog() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "4K VIP", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyanSecondary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, CyanSecondary)
                        ) {
                            Text(
                                text = "🔊 Audio: Mandarin (Original)",
                                color = CyanSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldPrimary)
                        ) {
                            Text(
                                text = "💬 Subtitle: Indonesia (Sub Indo)",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = episode.synopsis,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Episode Switcher Horizontal Scroll
            item {
                Text(
                    text = "Pilih Episode Lain:",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(donghua.episodes) { ep ->
                        val isCurrent = ep.id == episode.id
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) GoldPrimary else DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) GoldPrimary else DarkSurfaceHighlight
                            ),
                            modifier = Modifier
                                .width(70.dp)
                                .clickable { viewModel.selectEpisode(ep) }
                                .testTag("player_switch_ep_${ep.episodeNumber}")
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Ep ${ep.episodeNumber}",
                                    color = if (isCurrent) Color.Black else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (ep.isVipOnly) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "VIP",
                                        tint = if (isCurrent) Color.Black else GoldPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Download dialog
        if (showDownloadDialog) {
            DownloadEpisodeDialog(
                donghua = donghua,
                episode = episode,
                isUserVip = subscription.isVip,
                onDismiss = { viewModel.closeDownloadDialog() },
                onDownloadSelected = { quality ->
                    viewModel.downloadEpisode(donghua, episode, quality)
                },
                onUpgradeVipClick = {
                    viewModel.closeDownloadDialog()
                    viewModel.openVipDialog()
                }
            )
        }
    }
}

private fun getDynamicSubtitle(donghuaId: String, positionSec: Int, subtitleLang: String): String {
    if (subtitleLang == "Nonaktif") return ""
    if (subtitleLang == "English") {
        val lines = listOf(
            "[EN] Senior Elder, let me handle this sword formation!",
            "[EN] Supreme Golden Dragon Technique: Pierce the Nine Heavens!",
            "[EN] With my Hegemon Overlord body, no ancient god can stop me!",
            "[EN] Surrender the Celestial Spirit Pill, or perish right here!",
            "[EN] This level of cultivation has exceeded the boundaries of mortals!",
            "[EN] Emperor Shi Hao, take the ultimate strike of the Eight Lower Realms!",
            "[EN] My destiny is decided by myself, not by the heavens!",
            "[EN] Activate the ancient sect protection seal immediately!",
            "[EN] Step back! This ancient demonic beast is waking up!"
        )
        val index = (positionSec / 5) % lines.size
        return lines[index]
    }
    if (subtitleLang == "Hanzi (中文)") {
        val lines = listOf(
            "[中] 大长老，这道万剑归宗剑阵就交给我吧！",
            "[中] 太古金龙绝技：贯穿九霄神界！",
            "[中] 凭我的至尊霸体，诸天神魔谁敢挡我！",
            "[中] 交出九转天灵丹，否则今日便是你的死期！",
            "[中] 此等通天修为，早已超脱凡尘极限！",
            "[中] 荒天帝，接下下界八域这最强的一击吧！",
            "[中] 我命由我不由天，天若阻我我便逆天！",
            "[中] 速速开启宗门护山大阵，迎战强敌！",
            "[中] 退后！上古凶兽要苏醒了！"
        )
        val index = (positionSec / 5) % lines.size
        return lines[index]
    }

    // Default Indonesian Subtitles (Sub Indo)
    val lines = when (donghuaId) {
        "tales_of_herding_gods" -> listOf(
            "[Sub Indo] Qin Mu: \"Kakek Kepala Desa, teknik Pedang Jiwa ini terasa mengalir bersama darahku!\"",
            "[Sub Indo] Nenek Si: \"Anak nakal, jangan meremehkan Reruntuhan Besar. Di luar sana penuh dewa palsu!\"",
            "[Sub Indo] Qin Mu: \"Tubuh Hegemon Overlord milikku tak akan pernah tunduk pada takdir semu!\"",
            "[Sub Indo] Tetua Ma: \"Fokuskan tinjumu! Di alam para dewa, hanya kekuatan murni yang berbicara!\"",
            "[Sub Indo] Qin Mu: \"Ayo maju! Biarkan aku menggembala para dewa surga yang congkak itu!\""
        )
        "renegade_immortal" -> listOf(
            "[Sub Indo] Wang Lin: \"Jika surga ingin merampas keluargaku, aku akan membantai surga!\"",
            "[Sub Indo] Situ Nan: \"Bagus, bocah! Jalan kultivasi adalah jalan pembunuhan tanpa belas kasihan!\"",
            "[Sub Indo] Wang Lin: \"Manik Penentang Surga ini adalah takdirku menjadi penguasa semesta!\"",
            "[Sub Indo] Wang Lin: \"Teng Huayuan! Bayar darah keluargaku dengan nyawa seluruh klanmu!\"",
            "[Sub Indo] Wang Lin: \"Kultivasi Kehancuran Jiwa: Semua makhluk fana, tunduklah padaku!\""
        )
        "perfect_world" -> listOf(
            "[Sub Indo] Shi Hao: \"Siapa yang berani mengaku tak terkalahkan di hadapan Kaisar Huang?!\"",
            "[Sub Indo] Shi Hao: \"Tulang Tertinggiku mungkin dirampas, tapi tekadku menembus Sembilan Surga!\"",
            "[Sub Indo] Shi Hao: \"Kuali Semesta Sejati: Hancurkan semua tetua klan munafik ini!\"",
            "[Sub Indo] Shi Hao: \"Di Delapan Wilayah Bawah ini, akulah yang menentukan aturan hidup dan mati!\"",
            "[Sub Indo] Shi Hao: \"Bahkan jika aku harus menopang langit seorang diri, aku takkan mundur!\""
        )
        "swallowed_star" -> listOf(
            "[Sub Indo] Luo Feng: \"Kekuatan Psikis Bintang, aktifkan Pisau Terbang Bayangan!\"",
            "[Sub Indo] Babata: \"Luo Feng, potensi genetikmu melampaui standar Petarung Planet Bintang!\"",
            "[Sub Indo] Luo Feng: \"Binatang Monster Emas ini tidak boleh sampai menyentuh pangkalan kota manusia!\"",
            "[Sub Indo] Luo Feng: \"Dengan warisan Planet Yun Mo, aku akan melangkah ke panggung alam semesta!\"",
            "[Sub Indo] Luo Feng: \"Formasi Sembilan Tingkat Pembelah Bintang: Luncurkan tembakan meriam laser!\""
        )
        "soul_land" -> listOf(
            "[Sub Indo] Tang San: \"Sekte Tang tidak akan pernah runtuh! Terimalah Palu Clear Sky milikku!\"",
            "[Sub Indo] Xiao Wu: \"Ge, apa pun yang terjadi, aku akan selalu berada di sampingmu.\"",
            "[Sub Indo] Tang San: \"Domain Perak Biru: Mengikat seluruh medan pertempuran Aula Roh!\"",
            "[Sub Indo] Dai Mubai: \"Tujuh Iblis Shrek, bersiap untuk fusi kekuatan jiwa roh dewa!\"",
            "[Sub Indo] Tang San: \"Senjata Rahasia Sekte Tang: Teratai Emas Buddha Yang Mengamuk!\""
        )
        "btth" -> listOf(
            "[Sub Indo] Xiao Yan: \"Tiga puluh tahun di timur sungai, tiga puluh tahun di barat! Jangan remehkan pemuda miskin!\"",
            "[Sub Indo] Yao Lao: \"Bocah, kendalikan Api Surgawi itu dengan Mantra Fen Jue!\"",
            "[Sub Indo] Xiao Yan: \"Nalan Yanran, perjanjian tiga tahun kita berakhir di Puncak Sekte Yunlan hari ini!\"",
            "[Sub Indo] Xiao Yan: \"Teratai Api Buddha yang Mengamuk (Buddha's Angry Lotus): Musnahkan semuanya!\"",
            "[Sub Indo] Xiao Yan: \"Demi melindungi Medusa dan Sekte Xiao, aku akan membakar seluruh Sekte Jiwa!\""
        )
        "nezha_movie" -> listOf(
            "[Sub Indo] Nezha: \"Takdirku ditentukan olehku sendiri, bukan oleh surga atau dewa mana pun!\"",
            "[Sub Indo] Ao Bing: \"Nezha, jika kau menahan sambaran petir itu sendirian, jiwamu akan hancur!\"",
            "[Sub Indo] Nezha: \"Jika orang lain menganggapku iblis, maka aku akan menjadi iblis pembasmi kejahatan!\"",
            "[Sub Indo] Taiyi Zhenren: \"Aduh celaka, mantra pengubah takdir ini butuh keteguhan hati murni!\"",
            "[Sub Indo] Nezha: \"Ao Bing! Ayo kita hadapi bencana petir langit sembilan lapis ini bersama!\""
        )
        else -> listOf(
            "[Sub Indo] Pendekar: \"Tetua Agung, formasi pedang spiritual ini telah diaktifkan sepenuhnya!\"",
            "[Sub Indo] Kultivator: \"Teknik Rahasia Naga Emas: Menembus Sembilan Langit dan Membelah Bumi!\"",
            "[Sub Indo] Pendekar: \"Dengan tubuh kultivasi ini, tak seorang pun dewa purba yang mampu menghentikanku!\"",
            "[Sub Indo] Musuh: \"Serahkan Pil Roh Surgawi dan Pusaka Abadi itu, atau binasalah di sini!\"",
            "[Sub Indo] Kultivator: \"Kekuatan kultivasi ini telah melampaui batas fana dan menembus Alam Dewa!\"",
            "[Sub Indo] Pendekar: \"Keluarkan jurus pamungkasmu! Pertarungan ini baru saja dimulai!\"",
            "[Sub Indo] Kultivator: \"Segel perlindungan sekte telah terbuka, bersiaplah menyambut pertempuran besar!\""
        )
    }
    val index = (positionSec / 5) % lines.size
    return lines[index]
}
