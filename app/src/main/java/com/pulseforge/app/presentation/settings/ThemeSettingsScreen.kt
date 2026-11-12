package com.pulseforge.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.PulseThemeColors
import com.pulseforge.app.domain.model.ThemePalette
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader

import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.runtime.*

data class ThemePreviewItem(
    val palette: ThemePalette,
    val primaryColor: Color,
    val secondaryColor: Color,
    val bgColor: Color
)

@Composable
fun ThemeSettingsScreen(
    activeTheme: ThemePalette,
    isDarkMode: Boolean,
    manifestUrl: String = "",
    onSelectTheme: (ThemePalette) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onCheckForUpdates: () -> Unit = {},
    onTriggerDemoUpdate: () -> Unit = {},
    onUpdateManifestUrl: (String) -> Unit = {}
) {
    var customUrl by remember { mutableStateOf(manifestUrl) }

    val themePreviews = listOf(
        ThemePreviewItem(ThemePalette.POWER, Color(0xFF00FF87), Color(0xFF00E5FF), Color(0xFF090A0D)),
        ThemePreviewItem(ThemePalette.FIRE, Color(0xFFFF643D), Color(0xFFF59E0B), Color(0xFF0E0E12)),
        ThemePreviewItem(ThemePalette.CALM, Color(0xFF10B981), Color(0xFFFBBF24), Color(0xFF070E0B)),
        ThemePreviewItem(ThemePalette.PEACE, Color(0xFF3B82F6), Color(0xFF38BDF8), Color(0xFF0A0E17)),
        ThemePreviewItem(ThemePalette.WARM, Color(0xFFFA114F), Color(0xFFA1FF00), Color(0xFF050505)),
        ThemePreviewItem(ThemePalette.MOOD, Color(0xFFFFFFFF), Color(0xFF94A3B8), Color(0xFF0C0C0E)),
        ThemePreviewItem(ThemePalette.LIGHT, Color(0xFF2563EB), Color(0xFF0284C7), Color(0xFFF8FAFC))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "UI Themes & Aesthetics",
                subtitle = "Choose an atmospheric color scheme tailored to your training vibe"
            )
        }

        // Dark / Light Mode Switch
        item {
            PulseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dark Mode Interface",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isDarkMode) "OLED-friendly dark palette active" else "Crisp high-key daylight mode active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }
        }

        // Web OTA In-App Update Center
        item {
            PulseCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Web OTA App Updates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current: v${com.pulseforge.app.BuildConfig.VERSION_NAME} (Build ${com.pulseforge.app.BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customUrl,
                    onValueChange = {
                        customUrl = it
                        onUpdateManifestUrl(it)
                    },
                    label = { Text("Update Manifest URL (version.json)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onCheckForUpdates,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check Web")
                    }
                    OutlinedButton(
                        onClick = onTriggerDemoUpdate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Update")
                    }
                }
            }
        }

        // Themes List Header
        item {
            Text(
                text = "Atmospheric Palettes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Themes List
        items(themePreviews) { item ->
            val isSelected = item.palette == activeTheme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTheme(item.palette) }
                    .then(
                        if (isSelected) {
                            Modifier.border(2.dp, item.primaryColor, RoundedCornerShape(16.dp))
                        } else {
                            Modifier.border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        }
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Color Swatches
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.bgColor)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(item.primaryColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(item.secondaryColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = item.palette.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) item.primaryColor else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.palette.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = item.primaryColor
                        )
                    }
                }
            }
        }
    }
}

