package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donghua
import com.example.data.model.Episode
import com.example.ui.DonghuaViewModel
import com.example.ui.Screen
import com.example.ui.components.DonghuaPosterCard
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

@Composable
fun DetailScreen(
    donghuaId: String,
    viewModel: DonghuaViewModel,
    modifier: Modifier = Modifier
) {
    val donghua = viewModel.repository.getDonghuaById(donghuaId)
    val subscription by viewModel.userSubscription.collectAsState()
    val isFavorite by viewModel.repository.isFavorite(donghuaId).collectAsState(initial = false)
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()

    var selectedEpisodeForDownload by remember { mutableStateOf<Episode?>(null) }
    var isSynopsisExpanded by remember { mutableStateOf(false) }

    if (donghua == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Text(text = "Donghua tidak ditemukan", color = TextPrimary)
        }
        return
    }

    val relatedList = viewModel.repository.getRelatedDonghua(donghua)

    Box(modifier = modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // --- Hero Top Banner & Controls ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(donghua.posterColorHex), DarkBackground)
                            )
                        )
                ) {
                    // Actual Poster/Banner image
                    if (donghua.bannerDrawableRes != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = donghua.bannerDrawableRes),
                            contentDescription = donghua.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else if (donghua.posterDrawableRes != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = donghua.posterDrawableRes),
                            contentDescription = donghua.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        // Chinese title watermark
                        Text(
                            text = donghua.chineseTitle,
                            color = Color.White.copy(alpha = 0.15f),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }

                    // Scrim gradient for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        DarkBackground.copy(alpha = 0.8f),
                                        DarkBackground
                                    )
                                )
                            )
                    )

                    // Top Bar (Back & Bookmark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = { viewModel.goBack() }, modifier = Modifier.testTag("detail_back_button")) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleFavorite(donghua.id, isFavorite) },
                                modifier = Modifier.testTag("detail_favorite_button")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Favorit",
                                    tint = if (isFavorite) GoldPrimary else TextPrimary
                                )
                            }
                        }
                    }

                    // Bottom info overlay
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VipBadge(text = "4K ULTRA HD")
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (donghua.isMovie) LotusPink else CyanSecondary
                            ) {
                                Text(
                                    text = if (donghua.isMovie) "FILM LAYAR LEBAR" else "${donghua.currentEpisodes}/${donghua.totalEpisodes} EPISODE",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = donghua.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${donghua.chineseTitle} • ${donghua.studio} (${donghua.releaseYear})",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // --- Stats & Genres Row ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${donghua.rating}/10", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(text = "•", color = TextSecondary)
                    Text(text = donghua.status, color = CyanSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "•", color = TextSecondary)
                    Text(text = "${donghua.viewCount} Penonton", color = TextSecondary, fontSize = 12.sp)
                }

                // Genre Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    donghua.genres.forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight)
                        ) {
                            Text(
                                text = genre,
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Audio & Subtitle Information Tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, CyanSecondary.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "🔊 Mandarin (Original)",
                            color = CyanSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldPrimary.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "💬 Subtitle Indonesia (Sub Indo 4K)",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // --- Action Buttons ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val firstEpisode = donghua.episodes.firstOrNull()
                    Button(
                        onClick = {
                            if (firstEpisode != null) {
                                viewModel.navigateTo(Screen.Player(donghua.id, firstEpisode.id))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("detail_watch_now_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (donghua.isMovie) "Tonton Film" else "Tonton Episode 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            selectedEpisodeForDownload = firstEpisode
                            viewModel.openDownloadDialog()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("detail_download_all_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Unduh", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- VIP Notice Banner ---
            if (!subscription.isVip) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { viewModel.openVipDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Tonton Tanpa Iklan & Akses 4K Ultra HD", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Langganan VIP mulai Rp 5.000 / 7 hari", color = TextSecondary, fontSize = 10.sp)
                            }
                            Text(text = "Beli VIP", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- Sinopsis ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Sinopsis Cerita",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = donghua.synopsis,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = if (isSynopsisExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isSynopsisExpanded) "Tampilkan Lebih Sedikit" else "Baca Selengkapnya",
                        color = GoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { isSynopsisExpanded = !isSynopsisExpanded }
                            .padding(top = 4.dp)
                    )
                }
            }

            // --- Episode List Section ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (donghua.isMovie) "Pilihan Film" else "Daftar Episode (${donghua.episodes.size})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kualitas 4K/1080p",
                        color = CyanSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            items(donghua.episodes) { episode ->
                EpisodeItemRow(
                    episode = episode,
                    isUserVip = subscription.isVip,
                    onPlayClick = {
                        if (episode.isVipOnly && !subscription.isVip) {
                            viewModel.openVipDialog()
                        } else {
                            viewModel.navigateTo(Screen.Player(donghua.id, episode.id))
                        }
                    },
                    onDownloadClick = {
                        selectedEpisodeForDownload = episode
                        viewModel.openDownloadDialog()
                    }
                )
            }

            // --- Rekomendasi Terkait ---
            if (relatedList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rekomendasi Donghua Terkait",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedList) { related ->
                            DonghuaPosterCard(
                                donghua = related,
                                onClick = { viewModel.navigateTo(Screen.Detail(related.id)) }
                            )
                        }
                    }
                }
            }
        }

        // Download Dialog Modal
        if (showDownloadDialog && selectedEpisodeForDownload != null) {
            DownloadEpisodeDialog(
                donghua = donghua,
                episode = selectedEpisodeForDownload!!,
                isUserVip = subscription.isVip,
                onDismiss = { viewModel.closeDownloadDialog() },
                onDownloadSelected = { quality ->
                    viewModel.downloadEpisode(donghua, selectedEpisodeForDownload!!, quality)
                },
                onUpgradeVipClick = {
                    viewModel.closeDownloadDialog()
                    viewModel.openVipDialog()
                }
            )
        }
    }
}

@Composable
fun EpisodeItemRow(
    episode: Episode,
    isUserVip: Boolean,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val isLocked = episode.isVipOnly && !isUserVip

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onPlayClick)
            .testTag("episode_item_${episode.episodeNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLocked) DarkSurfaceHighlight else GoldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isLocked) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "VIP", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = GoldPrimary, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = episode.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (episode.isNewlyReleased) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = "BARU",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        if (episode.isVipOnly) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VipBadge(text = "FAST TRACK", isCompact = true)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Durasi: ${episode.duration} • ${episode.releaseDateText ?: "Kualitas 4K"} • ~${episode.downloadSizeMb} MB",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.testTag("episode_download_btn_${episode.episodeNumber}")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Unduh Episode",
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
