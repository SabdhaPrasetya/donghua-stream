package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Donghua
import com.example.data.repository.DonghuaCatalog
import com.example.ui.DonghuaViewModel
import com.example.ui.Screen
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.DonghuaPosterCard
import com.example.ui.components.VipBadge
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LotusPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGoldGradientEnd
import com.example.ui.theme.VipGoldGradientStart

@Composable
fun HomeScreen(
    viewModel: DonghuaViewModel,
    modifier: Modifier = Modifier
) {
    val subscription by viewModel.userSubscription.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val allDonghua by viewModel.allDonghua.collectAsState()
    val bannerNotification by viewModel.bannerNotification.collectAsState()
    val selectedUploadDay by viewModel.selectedUploadDay.collectAsState()

    val featuredDonghua = allDonghua.find { it.id == "soul_land" } ?: allDonghua.first()
    val popularList = allDonghua.filter { !it.isMovie }.take(6)
    val moviesList = allDonghua.filter { it.isMovie }
    val cultivationList = allDonghua.filter { it.genres.contains("Kultivasi") }.take(6)
    val scheduledDonghuaList = if (selectedUploadDay == "Semua Hari") allDonghua else allDonghua.filter { it.uploadDay.equals(selectedUploadDay, ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- Top Bar Header ---
        item {
            HomeTopBar(
                isVip = subscription.isVip,
                onSearchClick = { viewModel.navigateTo(Screen.Explore) },
                onDownloadsClick = { viewModel.navigateTo(Screen.Downloads) },
                onVipClick = { viewModel.navigateTo(Screen.Profile) }
            )
        }

        // --- Live Notification Toast Banner ---
        if (bannerNotification != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF381E72),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bannerNotification ?: "",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = { viewModel.dismissBannerNotification() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // --- Hero Banner Carousel ---
        item {
            HeroBanner(
                donghua = featuredDonghua,
                onPlayClick = {
                    viewModel.navigateTo(Screen.Player(featuredDonghua.id, featuredDonghua.episodes.first().id))
                },
                onDetailClick = {
                    viewModel.navigateTo(Screen.Detail(featuredDonghua.id))
                }
            )
        }

        // --- Quick Genre Selector Bar ---
        item {
            QuickGenreBar(
                onGenreClick = { genre ->
                    viewModel.setSelectedGenre(genre)
                    viewModel.navigateTo(Screen.Explore)
                }
            )
        }

        // --- VIP Promo Banner (If non-VIP) ---
        if (!subscription.isVip) {
            item {
                VipPromoCard(
                    onUpgradeClick = { viewModel.openVipDialog() }
                )
            }
        }

        // --- Jadwal Upload & Rilis Harian (Upload Schedule Section) ---
        item {
            SectionHeader(
                title = "📅 Jadwal Upload & Rilis Harian",
                subtitle = "Jadwal episode baru setiap hari & simulasi rilis instan",
                icon = Icons.Default.CalendarMonth,
                onSeeAllClick = {
                    viewModel.navigateTo(Screen.Explore)
                }
            )

            // Day Selector Chips (Senin, Selasa, Rabu, dll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DonghuaCatalog.DAYS_OF_WEEK.forEach { day ->
                    val isSelected = selectedUploadDay == day
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GoldPrimary else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else DarkSurfaceHighlight
                        ),
                        modifier = Modifier
                            .clickable { viewModel.setSelectedUploadDay(day) }
                            .testTag("schedule_day_chip_$day")
                    ) {
                        Text(
                            text = day,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Scheduled items horizontal card list
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(scheduledDonghuaList) { donghua ->
                    ScheduleCard(
                        donghua = donghua,
                        onPlayClick = {
                            val targetEp = donghua.episodes.lastOrNull() ?: donghua.episodes.first()
                            viewModel.navigateTo(Screen.Player(donghua.id, targetEp.id))
                        },
                        onDetailClick = {
                            viewModel.navigateTo(Screen.Detail(donghua.id))
                        },
                        onSimulateReleaseClick = {
                            viewModel.triggerInstantRelease(donghua.id)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Continue Watching (Riwayat) ---
        if (watchHistory.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Lanjutkan Menonton",
                    subtitle = "Lanjutkan petualangan kultivasimu",
                    icon = Icons.Default.PlayArrow,
                    onSeeAllClick = { viewModel.navigateTo(Screen.History) }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchHistory) { item ->
                        ContinueWatchingCard(
                            item = item,
                            onClick = {
                                viewModel.navigateTo(Screen.Player(item.donghua.id, item.episode.id))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- Rekomendasi Berdasarkan Genre & Minat ---
        item {
            SectionHeader(
                title = "Rekomendasi Untuk Anda",
                subtitle = "Berdasarkan genre kultivasi & aksi favorit",
                icon = Icons.Default.Star,
                onSeeAllClick = { viewModel.navigateTo(Screen.Explore) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations) { donghua ->
                    DonghuaPosterCard(
                        donghua = donghua,
                        onClick = { viewModel.navigateTo(Screen.Detail(donghua.id)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Film Donghua Lengkap (Movies) ---
        item {
            SectionHeader(
                title = "Film Donghua Layar Lebar",
                subtitle = "Koleksi film animasi bioskop kualitas 4K",
                icon = Icons.Default.Movie,
                onSeeAllClick = {
                    viewModel.setSelectedGenre("Film Donghua")
                    viewModel.navigateTo(Screen.Explore)
                }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(moviesList) { movie ->
                    DonghuaPosterCard(
                        donghua = movie,
                        onClick = { viewModel.navigateTo(Screen.Detail(movie.id)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Sedang Populer & Trending ---
        item {
            SectionHeader(
                title = "Sedang Populer & Trending",
                subtitle = "Paling banyak ditonton minggu ini",
                icon = Icons.Default.LocalFireDepartment,
                onSeeAllClick = { viewModel.navigateTo(Screen.Explore) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularList) { donghua ->
                    DonghuaPosterCard(
                        donghua = donghua,
                        onClick = { viewModel.navigateTo(Screen.Detail(donghua.id)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Genre: Kultivasi & Xianxia Terhebat ---
        item {
            SectionHeader(
                title = "Kultivasi & Xianxia Terbaik",
                subtitle = "Mencapai keabadian dan menembus batas langit",
                icon = Icons.Default.Tv,
                onSeeAllClick = {
                    viewModel.setSelectedGenre("Kultivasi")
                    viewModel.navigateTo(Screen.Explore)
                }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cultivationList) { donghua ->
                    DonghuaPosterCard(
                        donghua = donghua,
                        onClick = { viewModel.navigateTo(Screen.Detail(donghua.id)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Studio Pilihan Populer ---
        item {
            SectionHeader(
                title = "Studio Animasi Populer",
                subtitle = "Pilih donghua berdasarkan studio produksi",
                icon = Icons.Default.Tv
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DonghuaCatalog.STUDIOS.filter { it != "Semua Studio" }) { studio ->
                    StudioCard(
                        studioName = studio,
                        onClick = {
                            viewModel.setSelectedStudio(studio)
                            viewModel.navigateTo(Screen.Explore)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeTopBar(
    isVip: Boolean,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onVipClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_donghua_logo),
                contentDescription = "Donghua Stream Logo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Donghua",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Stream",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "Streaming & Unduhan 4K",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isVip) {
                VipBadge(
                    text = "VIP AKTIF",
                    modifier = Modifier.clickable(onClick = onVipClick)
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(VipGoldGradientStart, VipGoldGradientEnd)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onVipClick)
                        .testTag("home_vip_badge_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIP Rp 5rb",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onDownloadsClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("home_downloads_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Unduhan Offline",
                    tint = TextPrimary
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("home_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari Donghua",
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
fun HeroBanner(
    donghua: Donghua,
    onPlayClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onDetailClick)
            .testTag("hero_banner_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            // Background Hero Image
            if (donghua.bannerDrawableRes != null) {
                Image(
                    painter = painterResource(id = donghua.bannerDrawableRes),
                    contentDescription = donghua.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(donghua.posterColorHex), DarkBackground)
                            )
                        )
                )
            }

            // Gradient scrim overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DarkBackground.copy(alpha = 0.4f),
                                DarkBackground.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary
                    ) {
                        Text(
                            text = "FEATURED",
                            color = Color(0xFF381E72),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = donghua.genres.firstOrNull()?.uppercase() ?: "ACTION",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${donghua.rating}",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = donghua.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Studio: ${donghua.studio}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF381E72)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("hero_play_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tonton Sekarang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onDetailClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.4f)),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(text = "Detail & Episode", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickGenreBar(onGenreClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DonghuaCatalog.GENRES.forEach { genre ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
                modifier = Modifier.clickable { onGenreClick(genre) }
            ) {
                Text(
                    text = genre,
                    color = if (genre == "Semua") GoldPrimary else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun VipPromoCard(onUpgradeClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF381E72), Color(0xFF2B2930), Color(0xFF4A4458))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onUpgradeClick)
            .testTag("vip_promo_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Paket VIP Mulai Rp 5.000 / 7 Hari", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bebas nonton tanpa iklan, kualitas 4K & download sepuasnya!",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldPrimary
            ) {
                Text(
                    text = "Aktifkan",
                    color = Color(0xFF381E72),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        if (onSeeAllClick != null) {
            Text(
                text = "Lihat Semua",
                color = GoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onSeeAllClick)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun StudioCard(studioName: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Tv, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = studioName,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Produksi Resmi",
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun ScheduleCard(
    donghua: Donghua,
    onPlayClick: () -> Unit,
    onDetailClick: () -> Unit,
    onSimulateReleaseClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (donghua.isRecentlyUpdated) GoldPrimary.copy(alpha = 0.8f) else DarkSurfaceHighlight
        ),
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onDetailClick)
            .testTag("schedule_card_${donghua.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header: Day & Time Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${donghua.uploadDay} ${donghua.uploadTime}",
                            color = GoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (donghua.isRecentlyUpdated) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEF4444)
                    ) {
                        Text(
                            text = "BARU RILIS",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Poster & Episode info row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(donghua.posterColorHex),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donghua.title,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ep ${donghua.currentEpisodes}/${donghua.totalEpisodes}",
                        color = CyanSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = donghua.studio,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = donghua.latestEpisodeUpdateNote,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF381E72)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(32.dp)
                        .testTag("play_schedule_${donghua.id}")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Putar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Instant Release trigger button ("⚡ Rilis Baru")
                OutlinedButton(
                    onClick = onSimulateReleaseClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LilacPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .testTag("instant_release_${donghua.id}")
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Rilis", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
