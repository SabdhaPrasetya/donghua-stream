package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import java.util.Locale
import com.example.data.model.Donghua
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.Episode
import com.example.data.model.PaymentMethod
import com.example.data.model.VideoQuality
import com.example.data.model.VipPlan
import com.example.data.repository.DonghuaCatalog
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LotusPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGoldGradientEnd
import com.example.ui.theme.VipGoldGradientStart

@Composable
fun VipBadge(
    modifier: Modifier = Modifier,
    text: String = "VIP 4K",
    isCompact: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(VipGoldGradientStart, VipGoldGradientEnd)
                ),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(if (isCompact) 11.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = text,
                color = Color.Black,
                fontSize = if (isCompact) 9.sp else 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DonghuaPosterCard(
    donghua: Donghua,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clickable(onClick = onClick)
            .testTag("donghua_card_${donghua.id}")
    ) {
        // Poster Image Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(donghua.posterColorHex),
                            DarkSurface
                        )
                    )
                )
        ) {
            // Render actual poster artwork if available
            if (donghua.posterDrawableRes != null) {
                Image(
                    painter = painterResource(id = donghua.posterDrawableRes),
                    contentDescription = donghua.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (donghua.bannerDrawableRes != null) {
                Image(
                    painter = painterResource(id = donghua.bannerDrawableRes),
                    contentDescription = donghua.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (!donghua.posterUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = donghua.posterUrl,
                    contentDescription = donghua.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // High-End Artistic Stylized Poster Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(donghua.posterColorHex).copy(alpha = 0.9f),
                                    Color(donghua.badgeColorHex).copy(alpha = 0.5f),
                                    Color(0xFF0F0F1A)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Chinese Calligraphy Watermark Seal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(donghua.badgeColorHex).copy(alpha = 0.6f)),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = donghua.chineseTitle.take(2),
                                    color = GoldPrimary.copy(alpha = 0.95f),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = donghua.chineseTitle,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Dark subtle gradient overlay at top for badge legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Dark vignette gradient overlay at bottom for episode text legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Badges Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top-Left Badge (Purple / Custom Pill)
                Surface(
                    shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp, topEnd = 3.dp, bottomStart = 3.dp),
                    color = Color(donghua.badgeColorHex),
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = donghua.badgeText ?: if (donghua.isMovie) "Film" else "New",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }

                // Top-Right Rating Badge (Dark Pill with Yellow Star)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.68f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = "★",
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f", donghua.rating),
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom-Left Overlay (Episode Count & Sub Indo Badge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 7.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (donghua.isMovie) "Film" else "${donghua.currentEpisodes} Eps",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.9f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyanSecondary.copy(alpha = 0.95f)
                ) {
                    Text(
                        text = "Sub Indo",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                    )
                }
            }
        }

        // Info Section below poster
        Column(
            modifier = Modifier.padding(top = 6.dp, start = 2.dp, end = 2.dp, bottom = 4.dp)
        ) {
            Text(
                text = donghua.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = donghua.genres.take(2).joinToString(" • "),
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ContinueWatchingCard(
    item: com.example.data.model.WatchHistoryItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
            .testTag("continue_watch_${item.donghua.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(Color(item.donghua.posterColorHex))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
                LinearProgressIndicator(
                    progress = { item.progressPercentage },
                    color = GoldPrimary,
                    trackColor = DarkSurfaceHighlight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.donghua.title,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ep ${item.episode.episodeNumber} • ${item.progressSeconds / 60}:${String.format("%02d", item.progressSeconds % 60)}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DonghuaHorizontalCard(
    donghua: Donghua,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("donghua_row_${donghua.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail Box
            Box(
                modifier = Modifier
                    .size(width = 85.dp, height = 115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(donghua.posterColorHex),
                                DarkSurface
                            )
                        )
                    )
            ) {
                if (donghua.posterDrawableRes != null) {
                    Image(
                        painter = painterResource(id = donghua.posterDrawableRes),
                        contentDescription = donghua.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (donghua.bannerDrawableRes != null) {
                    Image(
                        painter = painterResource(id = donghua.bannerDrawableRes),
                        contentDescription = donghua.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!donghua.posterUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = donghua.posterUrl,
                        contentDescription = donghua.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = donghua.chineseTitle,
                        color = Color.White.copy(alpha = 0.2f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Top Badge
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = Color(donghua.badgeColorHex),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = donghua.badgeText ?: if (donghua.isMovie) "Film" else "New",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = donghua.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "★",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f", donghua.rating),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${donghua.chineseTitle} • ${donghua.studio}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = donghua.synopsis,
                    color = TextSecondary.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    donghua.genres.take(3).forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkSurfaceHighlight
                        ) {
                            Text(
                                text = genre,
                                color = GoldPrimary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (donghua.isMovie) "${donghua.durationMinutes ?: 100} Mnt" else "Ep ${donghua.currentEpisodes}/${donghua.totalEpisodes}",
                        color = CyanSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadEpisodeDialog(
    donghua: Donghua,
    episode: Episode,
    isUserVip: Boolean,
    onDismiss: () -> Unit,
    onDownloadSelected: (VideoQuality) -> Unit,
    onUpgradeVipClick: () -> Unit
) {
    var selectedQuality by remember { mutableStateOf(if (isUserVip) VideoQuality.UHD_4K else VideoQuality.HD_720P) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unduh untuk Nonton Offline",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${donghua.title} - ${episode.title}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pilih Kualitas Video:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Quality list
                VideoQuality.entries.forEach { quality ->
                    val isLocked = quality.requiresVip && !isUserVip
                    val isSelected = selectedQuality == quality

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else DarkSurfaceHighlight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (isLocked) {
                                    onUpgradeVipClick()
                                } else {
                                    selectedQuality = quality
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = quality.label,
                                        color = if (isSelected) GoldPrimary else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (quality.requiresVip) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VipBadge(text = "VIP EXCLUSIVE", isCompact = true)
                                    }
                                }
                                Text(
                                    text = "Resolusi: ${quality.resolution} • Bitrate: ${quality.bitRate}",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }

                            val sizeMb = when (quality) {
                                VideoQuality.SD_360P -> (episode.downloadSizeMb * 0.5).toInt()
                                VideoQuality.HD_720P -> episode.downloadSizeMb
                                VideoQuality.FHD_1080P -> (episode.downloadSizeMb * 2.2).toInt()
                                VideoQuality.UHD_4K -> (episode.downloadSizeMb * 5.5).toInt()
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "~$sizeMb MB",
                                    color = if (isSelected) GoldPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Terkunci VIP",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Dipilih",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Button(
                    onClick = { onDownloadSelected(selectedQuality) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_download_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mulai Unduh Offline (${selectedQuality.label})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VipSubscriptionDialog(
    selectedPlan: VipPlan,
    selectedPaymentMethod: PaymentMethod,
    userExp: Int = 0,
    discountPercent: Int = 0,
    isProcessing: Boolean,
    successMessage: String?,
    onPlanSelected: (VipPlan) -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Langganan VIP Donghua",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Nonton Tanpa Iklan & Kualitas 4K Ultra HD",
                                color = GoldPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    if (!isProcessing) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (successMessage != null) {
                    // Success View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyanSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aktivasi VIP Berhasil!",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = successMessage,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(text = "Mulai Nonton Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // EXP Discount notice if applicable
                    if (discountPercent > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✨", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EXP Kultivasi ($userExp EXP): Anda berhak mendapatkan Diskon VIP $discountPercent%!",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // VIP Benefits List
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            BenefitRow(icon = Icons.Default.WorkspacePremium, title = "Akses Kualitas 4K Ultra HD & 1080p 60fps")
                            BenefitRow(icon = Icons.Default.Close, title = "Bebas Nonton 100% Tanpa Iklan (0 Iklan)")
                            BenefitRow(icon = Icons.Default.Download, title = "Download Episode Kecepatan Penuh Offline")
                            BenefitRow(icon = Icons.Default.Tv, title = "Akses Fast Track Rilis Episode Eksklusif")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Pilih Paket VIP:",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // 3 Plans with EXP discounts
                    DonghuaCatalog.VIP_PLANS.forEach { plan ->
                        val isSelected = selectedPlan.id == plan.id
                        val discountedPriceFormatted = plan.getDiscountedPriceFormatted(discountPercent)
                        val hasDiscount = discountPercent > 0

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) GoldPrimary else DarkSurfaceHighlight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onPlanSelected(plan) }
                                .testTag("vip_plan_${plan.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = plan.name,
                                            color = if (isSelected) GoldPrimary else TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (plan.discountBadge != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (plan.isPopular) GoldPrimary else CyanSecondary
                                            ) {
                                                Text(
                                                    text = plan.discountBadge,
                                                    color = Color.Black,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${plan.durationDays} Hari Akses Penuh",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = discountedPriceFormatted,
                                        color = if (isSelected) GoldPrimary else TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    if (hasDiscount) {
                                        Text(
                                            text = plan.priceFormatted,
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Metode Pembayaran (GoPay):",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // GoPay Account Details Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0081A0).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00AED6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF00AED6)
                                    ) {
                                        Text(
                                            text = "gopay",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = PaymentMethod.GOPAY_ACCOUNT_NAME,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("GoPay Nomor", PaymentMethod.GOPAY_NUMBER)
                                        clipboard.setPrimaryClip(clip)
                                        copiedToClipboard = true
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.QrCode2,
                                        contentDescription = "Salin",
                                        tint = Color(0xFF00AED6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No. GoPay: ${PaymentMethod.GOPAY_NUMBER}",
                                color = Color(0xFF00AED6),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Purchase Button
                    val finalPriceFormatted = selectedPlan.getDiscountedPriceFormatted(discountPercent)
                    Button(
                        onClick = onConfirmPayment,
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("pay_vip_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Memverifikasi GoPay...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bayar via GoPay • $finalPriceFormatted",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = CyanSecondary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun AdOverlay(
    countdown: Int,
    onSkipClick: () -> Unit,
    onUpgradeVipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Black.copy(alpha = 0.85f),
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sponsor Donghua Stream",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nikmati pengalaman menonton Donghua tanpa jeda iklan dengan langganan VIP (Mulai Rp 5.000)",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onUpgradeVipClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("ad_upgrade_vip_button")
                ) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Hapus Iklan (VIP Rp 5rb)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSkipClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary),
                    modifier = Modifier.testTag("ad_skip_button")
                ) {
                    Text(
                        text = if (countdown > 0) "Lewati ($countdown s)" else "Lewati Iklan",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
