package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.DonghuaViewModel
import com.example.ui.Screen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScheduleScreen(
    viewModel: DonghuaViewModel,
    modifier: Modifier = Modifier
) {
    val daysList = remember {
        listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    }

    var selectedDay by remember { mutableStateOf("Kamis") }
    val allDonghua by viewModel.allDonghua.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val subscription by viewModel.userSubscription.collectAsState()

    val currentDayList = remember(selectedDay, allDonghua) {
        allDonghua.filter { it.uploadDay.equals(selectedDay, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- Top Bar: "Jadwal Tayang" with Search and Avatar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jadwal Tayang",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("schedule_title")
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Explore) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari Donghua",
                        tint = LilacPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    shape = CircleShape,
                    color = if (subscription.isVip) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceHighlight,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { viewModel.navigateTo(Screen.Profile) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (currentUser.isOwner) {
                            Text(
                                text = "👑",
                                fontSize = 16.sp
                            )
                        } else if (subscription.isVip) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Sub-Header: Day Title & Total Anime Count (e.g. "Kamis (15 Anime)") ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDay,
                color = Color(0xFF9333EA), // Purple / Lilac from screenshot
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${currentDayList.size} Anime)",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Main Content: Left 2-Column Grid + Right Day Selector Sidebar ---
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            // Left: 2-Column Grid of Donghua for the selected day
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 8.dp)
            ) {
                AnimatedContent(
                    targetState = selectedDay,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScheduleGridTransition"
                ) { targetDay ->
                    val dayItems = allDonghua.filter { it.uploadDay.equals(targetDay, ignoreCase = true) }

                    if (dayItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Tidak ada rilis di hari $targetDay",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(dayItems, key = { it.id }) { donghua ->
                                ScheduleDonghuaCard(
                                    donghua = donghua,
                                    onClick = { viewModel.navigateTo(Screen.Detail(donghua.id)) }
                                )
                            }
                        }
                    }
                }
            }

            // Right: Vertical Pill Day Selector Column
            Column(
                modifier = Modifier
                    .width(86.dp)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                daysList.forEach { day ->
                    val isSelected = selectedDay.equals(day, ignoreCase = true)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF1E1B2E),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFFA855F7) else DarkSurfaceHighlight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clickable { selectedDay = day }
                            .testTag("schedule_day_btn_$day")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleDonghuaCard(
    donghua: Donghua,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("schedule_donghua_card_${donghua.id}")
    ) {
        // Poster Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(donghua.posterColorHex),
                            Color(0xFF0F172A)
                        )
                    )
                )
        ) {
            // Calligraphy / Artwork Simulation in background
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = donghua.chineseTitle.take(4),
                    color = Color.White.copy(alpha = 0.25f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = donghua.studio.take(12),
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            // Top-Right Star Rating Badge (e.g. "★ 7.62")
            Surface(
                shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "%.2f".format(donghua.rating),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Status badges container at bottom of poster
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            ) {
                if (donghua.isRecentlyUpdated || donghua.latestEpisodeUpdateNote.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF7C3AED), // Purple pill from screenshot
                        modifier = Modifier.padding(bottom = 3.dp)
                    ) {
                        Text(
                            text = if (donghua.latestEpisodeUpdateNote.isNotBlank()) donghua.latestEpisodeUpdateNote else "Sudah Update!",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Episode Count Badge (e.g. "26 Eps", "156 Eps")
                Text(
                    text = "${donghua.currentEpisodes} Eps",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title underneath poster
        Text(
            text = donghua.title,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
