package com.pulseforge.app.presentation.auth

import android.accounts.AccountManager
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.R
import com.pulseforge.app.core.auth.AuthAccount

private data class WelcomeSlide(
    val title: String,
    val subtitle: String,
    val topColor: Color,
    val bottomColor: Color,
    val accentColor: Color,
    val badgeLabel: String,
    val heroIcon: ImageVector
)

private enum class AuthModalMode {
    NONE,
    LOGIN,
    SIGNUP
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AuthScreen(
    onSignUp: (fullName: String, email: String, password: String) -> Result<AuthAccount>,
    onLogin: (identifier: String, password: String) -> Result<AuthAccount>,
    onGoogleSignIn: (googleEmail: String) -> Result<Pair<AuthAccount, Boolean>> = {
        Result.failure(IllegalStateException("Google sign-in unavailable"))
    },
    onAuthSuccess: (isNewSignUp: Boolean) -> Unit
) {
    val slides = remember {
        listOf(
            WelcomeSlide(
                title = "Workout Anywhere",
                subtitle = "Choose your preferred location and do your workouts anytime that suits you!",
                topColor = Color(0xFF53259B),
                bottomColor = Color(0xFF280E57),
                accentColor = Color(0xFFB388FF),
                badgeLabel = "ADAPTIVE TRAINING",
                heroIcon = Icons.Default.FitnessCenter
            ),
            WelcomeSlide(
                title = "Stay Strong & Healthy",
                subtitle = "We want you to fully enjoy the program and stay healthy and positive!",
                topColor = Color(0xFF3E7FA8),
                bottomColor = Color(0xFF1B4563),
                accentColor = Color(0xFF7FD8FF),
                badgeLabel = "SENSORS & NUTRITION",
                heroIcon = Icons.Default.DirectionsRun
            ),
            WelcomeSlide(
                title = "Track Every Rep & Macro",
                subtitle = "Live hardware telemetry, AI meal scanning, and your personal User ID synced locally.",
                topColor = Color(0xFF23273A),
                bottomColor = Color(0xFF12131C),
                accentColor = Color(0xFFE85B37),
                badgeLabel = "PULSEFORGE AI",
                heroIcon = Icons.Default.AutoAwesome
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })
    var modalMode by remember { mutableStateOf(AuthModalMode.NONE) }
    var showGoogleEmailFallback by remember { mutableStateOf(false) }

    val googlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val email = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!email.isNullOrBlank()) {
                onGoogleSignIn(email).onSuccess { (_, isNewUser) ->
                    modalMode = AuthModalMode.NONE
                    onAuthSuccess(isNewUser)
                }
            } else {
                showGoogleEmailFallback = true
            }
        }
    }

    val launchGoogleSignIn = {
        try {
            val intent = AccountManager.newChooseAccountIntent(
                null,
                null,
                arrayOf("com.google"),
                null,
                null,
                null,
                null
            )
            googlePickerLauncher.launch(intent)
        } catch (_: Exception) {
            showGoogleEmailFallback = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-bleed swipeable hero pager matching reference UI
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val slide = slides[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(slide.topColor, slide.bottomColor)
                        )
                    )
            ) {
                // Soft ambient radial glow behind hero centerpiece
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .align(Alignment.Center)
                        .offset(y = (-60).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    slide.accentColor.copy(alpha = 0.28f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Hero athletic centerpiece
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-70).dp)
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "PulseForge AI",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Text(
                                text = slide.badgeLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.4.sp,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(164.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.heroIcon,
                            contentDescription = slide.title,
                            tint = Color.White,
                            modifier = Modifier.size(84.dp)
                        )
                    }
                }
            }
        }

        // Top-right SKIP > button
        TextButton(
            onClick = { modalMode = AuthModalMode.SIGNUP },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 16.dp)
        ) {
            Text(
                text = "S K I P  >",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }

        // Bottom overlay: Headline + Subtitle + Dots + login / signup pills
        val activeSlide = slides[pagerState.currentPage]
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = activeSlide.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = activeSlide.subtitle,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(22.dp))

            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isSelected) 22.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Side-by-side login & signup pill buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = { modalMode = AuthModalMode.LOGIN },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.55f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.08f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { modalMode = AuthModalMode.SIGNUP },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF16171B)
                    )
                ) {
                    Text(
                        text = "signup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Continue with Google Pill Button
            OutlinedButton(
                onClick = launchGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.14f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        if (showGoogleEmailFallback) {
            GoogleEmailFallbackDialog(
                onDismiss = { showGoogleEmailFallback = false },
                onSubmit = { email ->
                    onGoogleSignIn(email).fold(
                        onSuccess = { (_, isNewUser) ->
                            showGoogleEmailFallback = false
                            modalMode = AuthModalMode.NONE
                            onAuthSuccess(isNewUser)
                        },
                        onFailure = {}
                    )
                }
            )
        }

        // Login / Signup Modal Sheet
        if (modalMode != AuthModalMode.NONE) {
            AuthFormDialog(
                mode = modalMode,
                onSwitchMode = {
                    modalMode = if (modalMode == AuthModalMode.LOGIN) AuthModalMode.SIGNUP else AuthModalMode.LOGIN
                },
                onDismiss = { modalMode = AuthModalMode.NONE },
                onSignUp = onSignUp,
                onLogin = onLogin,
                onSuccess = { isNew ->
                    modalMode = AuthModalMode.NONE
                    onAuthSuccess(isNew)
                }
            )
        }
    }
}

@Composable
private fun AuthFormDialog(
    mode: AuthModalMode,
    onSwitchMode: () -> Unit,
    onDismiss: () -> Unit,
    onSignUp: (String, String, String) -> Result<AuthAccount>,
    onLogin: (String, String) -> Result<AuthAccount>,
    onSuccess: (Boolean) -> Unit
) {
    var fullName by remember(mode) { mutableStateOf("") }
    var identifier by remember(mode) { mutableStateOf("") }
    var password by remember(mode) { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember(mode) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (mode == AuthModalMode.SIGNUP) "Create PulseForge Account" else "Welcome Back",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = if (mode == AuthModalMode.SIGNUP) {
                        "We'll generate your unique User ID and guide you through your fitness profile."
                    } else {
                        "Sign in with your Email or PulseForge User ID (e.g. PF-123456)."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (mode == AuthModalMode.SIGNUP) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMessage = null },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it; errorMessage = null },
                    label = {
                        Text(if (mode == AuthModalMode.SIGNUP) "Email or Phone" else "Email or User ID (PF-XXXXXX)")
                    },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password (min 6 chars)") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = onSwitchMode,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = if (mode == AuthModalMode.SIGNUP) {
                            "Already have an account? Login"
                        } else {
                            "New to PulseForge? Sign up"
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mode == AuthModalMode.SIGNUP) {
                        onSignUp(fullName, identifier, password).fold(
                            onSuccess = { onSuccess(true) },
                            onFailure = { errorMessage = it.message ?: "Sign up failed" }
                        )
                    } else {
                        onLogin(identifier, password).fold(
                            onSuccess = { onSuccess(false) },
                            onFailure = { errorMessage = it.message ?: "Login failed" }
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (mode == AuthModalMode.SIGNUP) "Create User ID & Continue" else "Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GoogleEmailFallbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var googleEmail by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Continue with Google",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = googleEmail,
                    onValueChange = { googleEmail = it; errorText = null },
                    label = { Text("Google Email (e.g. name@gmail.com)") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clean = googleEmail.trim()
                    if (clean.contains("@") && clean.contains(".")) {
                        onSubmit(clean)
                    } else {
                        errorText = "Enter a valid Google email address."
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
