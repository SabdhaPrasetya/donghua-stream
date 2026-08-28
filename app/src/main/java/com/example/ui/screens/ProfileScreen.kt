package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AvatarPreset
import com.example.data.model.Donghua
import com.example.data.model.PaymentMethod
import com.example.data.model.UserAccount
import com.example.data.model.VipPlan
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
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LotusPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGoldGradientEnd
import com.example.ui.theme.VipGoldGradientStart

@Composable
fun ProfileScreen(
    viewModel: DonghuaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val subscription by viewModel.userSubscription.collectAsState()
    val favoriteDonghua by viewModel.favoriteDonghua.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val selectedPlan by viewModel.selectedVipPlan.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
    val paymentSuccessMessage by viewModel.paymentSuccessMessage.collectAsState()

    val context = LocalContext.current
    var copiedToClipboard by remember { mutableStateOf(false) }
    var wifiOnlyDownload by remember { mutableStateOf(true) }
    var cacheCleared by remember { mutableStateOf(false) }
    var showAvatarPickerDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setUserCustomAvatar(it.toString())
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- 1. Profile & Avatar Header ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                if (currentUser.isOwner) Color(0xFF451A03) else Color(0xFF2B2930),
                                DarkBackground
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar with click to edit
                            UserAvatarView(
                                user = currentUser,
                                isVip = subscription.isVip,
                                size = 68.dp,
                                onClick = { showAvatarPickerDialog = true }
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.name,
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (currentUser.isOwner) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VipBadge(text = "OWNER", isCompact = true)
                                    } else if (subscription.isVip) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VipBadge(text = "VIP", isCompact = true)
                                    }
                                }
                                Text(
                                    text = currentUser.email,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (currentUser.isOwner) "👑 Status: Owner (VIP Bebas Selamanya)"
                                    else if (subscription.isVip) "🌟 Status: VIP ${subscription.planName} (Sisa ${subscription.remainingDays} Hari)"
                                    else "Status: Pengguna Gratis",
                                    color = if (currentUser.isOwner || subscription.isVip) GoldPrimary else CyanSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LilacPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier.clickable { showAvatarPickerDialog = true }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Ganti Foto Profil", color = LilacPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Login / Owner Fast-Login Switcher
                    if (!currentUser.isLoggedIn) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openAuthDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("profile_login_btn")
                            ) {
                                Text(text = "Masuk Akun", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.loginAsOwner() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(38.dp)
                                    .testTag("profile_owner_login_btn")
                            ) {
                                Text(text = "👑 Masuk Owner", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!currentUser.isOwner) {
                                OutlinedButton(
                                    onClick = { viewModel.loginAsOwner() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(text = "👑 Beralih ke Owner (${UserAccount.OWNER_EMAIL})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                                ) {
                                    Text(
                                        text = "👑 Akun Terverifikasi Sebagai Pemilik Aplikasi",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(text = "Keluar", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 2. EXP & Cultivation Level & VIP Discount Card ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("exp_cultivation_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header EXP
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
                                    imageVector = Icons.Default.MilitaryTech,
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
                                    text = "Tingkat Kultivasi & EXP",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentUser.cultivationRank,
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Discount badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser.discountPercent > 0) GoldPrimary else DarkSurfaceHighlight
                        ) {
                            Text(
                                text = if (currentUser.discountPercent > 0) "DISKON ${currentUser.discountPercent}%" else "DISKON 0%",
                                color = if (currentUser.discountPercent > 0) Color.Black else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    val expProgress = (currentUser.exp / 100_000f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { expProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GoldPrimary,
                        trackColor = DarkSurfaceHighlight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "EXP Saat Ini: ${"%,d".format(currentUser.exp)} EXP",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Max: 100.000 EXP",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // EXP Tier explanation
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "✨ Keuntungan Diskon EXP Kultivasi:",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• 10.000 - 99.999 EXP: Potongan VIP 20% langsung\n• 100.000 EXP (Maksimal): Potongan VIP 30% Pasti Seumur Hidup",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // EXP Organic Info (No manual claim buttons, only earned organically)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cara Mendapatkan EXP Kultivasi:",
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Nonton Donghua: +150 EXP setiap episode & +50 EXP bertambah tiap durasi nonton\n• Langganan Paket VIP: Bonus +10.000 EXP langsung\n• EXP otomatis bertambah tanpa perlu klaim manual dan memberikan diskon permanen!",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 3. VIP Package Selection & GoPay Payment (Merged into Profile) ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("vip_payment_section")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Langganan VIP Donghua",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (currentUser.discountPercent > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanSecondary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanSecondary)
                            ) {
                                Text(
                                    text = "Hemat ${currentUser.discountPercent}% dengan EXP",
                                    color = CyanSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "100% Bebas Iklan, Kualitas 4K UHD, dan Akses Fast Track Episode Baru.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // If Owner, show special privilege notice
                    if (currentUser.isOwner) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Akun Pemilik (Owner) Terdeteksi!",
                                        color = GoldPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Email sabdhaprasetya@gmail.com memiliki status VIP Seumur Hidup bebas iklan dan tidak perlu membayar langganan lagi.",
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // 3 VIP Plans with EXP discount calculation
                        Text(
                            text = "Pilih Paket VIP:",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        DonghuaCatalog.VIP_PLANS.forEach { plan ->
                            val isSelected = selectedPlan.id == plan.id
                            val discountedPrice = plan.getDiscountedPriceFormatted(currentUser.discountPercent)
                            val hasDiscount = currentUser.discountPercent > 0

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) GoldPrimary else DarkSurfaceHighlight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { viewModel.selectVipPlan(plan) }
                                    .testTag("profile_plan_${plan.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = plan.name,
                                                color = if (isSelected) GoldPrimary else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (plan.isPopular) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GoldPrimary
                                                ) {
                                                    Text(
                                                        text = "POPULER",
                                                        color = Color.Black,
                                                        fontSize = 8.sp,
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
                                            text = discountedPrice,
                                            color = if (isSelected) GoldPrimary else TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        if (hasDiscount) {
                                            Text(
                                                text = plan.priceFormatted,
                                                color = TextSecondary,
                                                fontSize = 10.sp,
                                                style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // GoPay Details Section (Owner Payment)
                        Text(
                            text = "Metode Pembayaran (GoPay):",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0081A0).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00AED6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF00AED6)
                                        ) {
                                            Text(
                                                text = "gopay",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "GoPay Resmi Developer",
                                            color = Color(0xFF00AED6),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("GoPay Nomor", PaymentMethod.GOPAY_NUMBER)
                                            clipboard.setPrimaryClip(clip)
                                            copiedToClipboard = true
                                            viewModel.addExp(50, "Salin Nomor GoPay")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "Salin Nomor",
                                            tint = if (copiedToClipboard) CyanSecondary else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Atas Nama:", color = TextSecondary, fontSize = 11.sp)
                                    Text(text = PaymentMethod.GOPAY_ACCOUNT_NAME, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Nomor GoPay:", color = TextSecondary, fontSize = 11.sp)
                                    Text(text = PaymentMethod.GOPAY_NUMBER, color = Color(0xFF00AED6), fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }

                                if (copiedToClipboard) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "✓ Nomor ${PaymentMethod.GOPAY_NUMBER} berhasil disalin ke clipboard!",
                                        color = CyanSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Success confirmation banner if paid
                        if (paymentSuccessMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CyanSecondary.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanSecondary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = paymentSuccessMessage!!, color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Pay Button
                        val finalPrice = selectedPlan.getDiscountedPriceFormatted(currentUser.discountPercent)
                        Button(
                            onClick = { viewModel.processVipPayment() },
                            enabled = !isProcessingPayment,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pay_vip_profile_btn")
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Memverifikasi Pembayaran GoPay...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            } else {
                                Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bayar via GoPay • $finalPrice",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Donghua Favorit (Bookmarks) ---
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Donghua Favorit (${favoriteDonghua.size})", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (favoriteDonghua.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Belum ada donghua favorit. Tandai donghua yang kamu sukai di halaman detail.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(favoriteDonghua) { donghua ->
                        DonghuaPosterCard(
                            donghua = donghua,
                            onClick = { viewModel.navigateTo(Screen.Detail(donghua.id)) }
                        )
                    }
                }
            }
        }

        // --- 5. Riwayat Tontonan ---
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Riwayat Tontonan (${watchHistory.size})", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.History) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(text = "Lihat Semua", color = LilacPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(14.dp))
                    }
                    if (watchHistory.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearWatchHistory() }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Riwayat", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (watchHistory.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Belum ada riwayat tontonan. Tonton donghua untuk mendapatkan EXP Kultivasi!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
            }
        }

        // --- 6. App Settings & Info ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pengaturan Aplikasi",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Wi-Fi Only Download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Unduh Hanya Lewat Wi-Fi", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Hemat kuota data seluler", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = wifiOnlyDownload,
                            onCheckedChange = { wifiOnlyDownload = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cache Cleaner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cacheCleared = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Bersihkan Cache Streaming", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (cacheCleared) "Cache 0 MB (Bersih)" else "Cache video ~128 MB",
                                    color = if (cacheCleared) CyanSecondary else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(
                            text = if (cacheCleared) "Selesai" else "Bersihkan",
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // About & Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Donghua Stream v1.2.0", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Owner: Sabdha Prasetya Suroso (GoPay: 0895402865399)", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Avatar Picker Dialog
    if (showAvatarPickerDialog) {
        AvatarPickerDialog(
            currentUser = currentUser,
            onPickFromGallery = {
                galleryLauncher.launch("image/*")
                showAvatarPickerDialog = false
            },
            onSelectPreset = { presetId ->
                viewModel.setUserPresetAvatar(presetId)
                showAvatarPickerDialog = false
            },
            onResetToDefault = {
                viewModel.setUserPresetAvatar("xiao_yan")
                showAvatarPickerDialog = false
            },
            onDismiss = { showAvatarPickerDialog = false }
        )
    }
}

@Composable
fun UserAvatarView(
    user: UserAccount,
    isVip: Boolean,
    size: androidx.compose.ui.unit.Dp = 64.dp,
    onClick: (() -> Unit)? = null
) {
    val preset = UserAccount.BUILTIN_AVATARS.find { it.id == user.avatarPresetId }
    val borderColor = if (user.isOwner) GoldPrimary
    else if (preset != null) Color(preset.accentColor)
    else if (isVip) GoldPrimary
    else LilacPrimary

    Box(
        modifier = Modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("user_avatar_clickable")
    ) {
        Surface(
            shape = CircleShape,
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, borderColor),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (user.avatarUrl != null) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar Pengguna",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else if (user.isOwner) {
                    Text(text = "👑", fontSize = (size.value * 0.44).sp)
                } else if (preset != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    preset.gradientColors.map { Color(it) }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = preset.emoji, fontSize = (size.value * 0.42).sp)
                    }
                } else if (user.isGoogleUser) {
                    Text(text = "G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = (size.value * 0.4).sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isVip) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
        }

        // Camera / Edit overlay badge if clickable
        if (onClick != null) {
            Surface(
                shape = CircleShape,
                color = LilacPrimary,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBackground),
                modifier = Modifier
                    .size(size * 0.36f)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Ganti Foto Profil",
                        tint = Color.Black,
                        modifier = Modifier.size(size * 0.22f)
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarPickerDialog(
    currentUser: UserAccount,
    onPickFromGallery: () -> Unit,
    onSelectPreset: (String) -> Unit,
    onResetToDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Avatar Bawaan (10)", "Galeri HP")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Pilih Foto Profil", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = LilacPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = LilacPrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) LilacPrimary else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Presets Grid
                    Text(
                        text = "Pilih Karakter Kultivator Favorit:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(UserAccount.BUILTIN_AVATARS, key = { it.id }) { preset ->
                            val isSelected = currentUser.avatarPresetId == preset.id && currentUser.avatarUrl == null

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(preset.accentColor).copy(alpha = 0.2f) else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color(preset.accentColor) else DarkSurfaceHighlight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectPreset(preset.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp),
                                        color = Color.Transparent
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.linearGradient(
                                                        preset.gradientColors.map { Color(it) }
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = preset.emoji, fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = preset.characterName,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = preset.title,
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Dipilih",
                                            tint = Color(preset.accentColor),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Gallery Picker
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LilacPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = LilacPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Unggah dari Galeri Ponsel",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilih foto favorit Anda dari penyimpanan foto / galeri perangkat ini.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onPickFromGallery,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Buka Galeri Foto HP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (currentUser.avatarUrl != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onResetToDefault,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Hapus Foto Kustom & Pakai Avatar Bawaan", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tutup", color = LilacPrimary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp)
    )
}
