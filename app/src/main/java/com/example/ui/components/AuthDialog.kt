package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AuthMode
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthDialog(
    authMode: AuthMode,
    email: String,
    password: String,
    name: String,
    isPasswordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    isResetOtpSent: Boolean,
    onModeChange: (AuthMode) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onSendResetEmailClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LilacPrimary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("auth_dialog_surface")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (authMode == AuthMode.FORGOT_PASSWORD) {
                            IconButton(
                                onClick = { onModeChange(AuthMode.LOGIN) },
                                modifier = Modifier.size(32.dp).testTag("auth_back_to_login_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = when (authMode) {
                                AuthMode.LOGIN -> "Masuk ke Akun"
                                AuthMode.REGISTER -> "Daftar Akun Baru"
                                AuthMode.FORGOT_PASSWORD -> "Lupa Password"
                            },
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_auth_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs for Login / Register (Hidden on Forgot Password)
                if (authMode != AuthMode.FORGOT_PASSWORD) {
                    val tabIndex = if (authMode == AuthMode.LOGIN) 0 else 1
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = DarkSurfaceElevated,
                        contentColor = TextPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                                color = LilacPrimary,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = authMode == AuthMode.LOGIN,
                            onClick = { onModeChange(AuthMode.LOGIN) },
                            text = {
                                Text(
                                    text = "Masuk (Login)",
                                    fontWeight = if (authMode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_login")
                        )
                        Tab(
                            selected = authMode == AuthMode.REGISTER,
                            onClick = { onModeChange(AuthMode.REGISTER) },
                            text = {
                                Text(
                                    text = "Daftar (Register)",
                                    fontWeight = if (authMode == AuthMode.REGISTER) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_register")
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Error Message Display
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF7F1D1D).copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = errorMessage, color = Color(0xFFFCA5A5), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }

                // Success Message Display
                if (successMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = successMessage, color = Color(0xFF6EE7B7), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }

                // Form Content according to mode
                when (authMode) {
                    AuthMode.LOGIN -> {
                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text("Email", fontSize = 12.sp) },
                            placeholder = { Text("contoh@donghua.id", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field with Eye Icon Feature (Fitur Mata)
                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = { Text("Password", fontSize = 12.sp) },
                            placeholder = { Text("••••••••", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = onTogglePasswordVisibility,
                                    modifier = Modifier.testTag("toggle_password_visibility_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isPasswordVisible) "Sembunyikan Password" else "Tampilkan Password",
                                        tint = if (isPasswordVisible) GoldPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        // Forgot Password clickable link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onModeChange(AuthMode.FORGOT_PASSWORD) },
                                modifier = Modifier.testTag("forgot_password_link")
                            ) {
                                Text(
                                    text = "Lupa Password?",
                                    color = LilacPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Login Button
                        Button(
                            onClick = onLoginClick,
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_login_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(text = "Masuk Sekarang", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    AuthMode.REGISTER -> {
                        // Full Name Field
                        OutlinedTextField(
                            value = name,
                            onValueChange = onNameChange,
                            label = { Text("Nama Lengkap", fontSize = 12.sp) },
                            placeholder = { Text("cth: Xiao Tang", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_register_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text("Alamat Email", fontSize = 12.sp) },
                            placeholder = { Text("contoh@donghua.id", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_register_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field with Eye Icon
                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = { Text("Buat Password", fontSize = 12.sp) },
                            placeholder = { Text("Min. 6 Karakter", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = onTogglePasswordVisibility,
                                    modifier = Modifier.testTag("toggle_register_password_visibility_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Lihat Password",
                                        tint = if (isPasswordVisible) GoldPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onRegisterClick() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_register_password_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Button
                        Button(
                            onClick = onRegisterClick,
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_register_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(text = "Daftar Akun Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    AuthMode.FORGOT_PASSWORD -> {
                        Text(
                            text = "Masukkan alamat email Anda yang terdaftar untuk menerima tautan dan kode verifikasi reset password.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text("Email Terdaftar", fontSize = 12.sp) },
                            placeholder = { Text("contoh@donghua.id", color = TextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = LilacPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onSendResetEmailClick() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LilacPrimary,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_forgot_email_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSendResetEmailClick,
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("send_reset_password_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Kirim Link / Kode OTP Reset", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Google Sign-In Section (Always available)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(DarkSurfaceHighlight))
                    Text(
                        text = "  atau masuk dengan  ",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(DarkSurfaceHighlight))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Google Login Button
                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceHighlight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("google_login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google 'G' stylized badge
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    color = Color(0xFF4285F4),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Lanjutkan dengan Akun Google",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
