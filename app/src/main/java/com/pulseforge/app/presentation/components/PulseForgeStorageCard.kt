package com.pulseforge.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.StorageStatus

/**
 * Dedicated PulseForge AI Folder Storage & System Permissions Card.
 * Manages user storage access, folder synchronization, and device capability states.
 */
@Composable
fun PulseForgeStorageCard(
    storageStatus: StorageStatus,
    isCameraGranted: Boolean,
    hasUsageAccess: Boolean,
    onSyncNow: () -> Unit,
    onRequestCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF13151D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2E3D))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2ED573).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Storage Folder",
                            tint = Color(0xFF2ED573),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "USER & APP DATA DIRECTORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF2ED573)
                        )
                        Text(
                            text = "Dedicated Device Storage Folder",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF2ED573).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Folder Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2ED573),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Folder Path & Files Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1C28),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2E3D))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Folder",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "DIRECTORY PATH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = storageStatus.targetDirectoryPath.ifEmpty { "Documents/PulseForge AI/" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${storageStatus.syncedFileCount} data files synchronized (JSON)",
                            fontSize = 11.sp,
                            color = Color(0xFF00CEC9),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = storageStatus.lastSyncTimestamp,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Sync Button
            Button(
                onClick = onSyncNow,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2235)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383C54)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync",
                        tint = Color(0xFF2ED573),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Sync & Export All Data to PulseForge AI Folder",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Device Capabilities & Permissions Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DEVICE CAPABILITIES & INTEGRATION STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Storage Pill
                    CapabilityPill(
                        label = "Storage: Ready",
                        isActive = storageStatus.isDirectoryReady,
                        icon = Icons.Default.Folder,
                        modifier = Modifier.weight(1f)
                    )

                    // Camera Pill
                    CapabilityPill(
                        label = if (isCameraGranted) "Camera: Ready" else "Camera: Tap",
                        isActive = isCameraGranted,
                        icon = Icons.Default.CameraAlt,
                        onClick = if (!isCameraGranted) onRequestCamera else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sensors Pill
                    CapabilityPill(
                        label = "Sensors: 9 Streams",
                        isActive = true,
                        icon = Icons.Default.Sensors,
                        modifier = Modifier.weight(1f)
                    )

                    // Usage Telemetry Pill
                    CapabilityPill(
                        label = if (hasUsageAccess) "Usage: Linked" else "Usage: Pending",
                        isActive = hasUsageAccess,
                        icon = Icons.Default.QueryStats,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CapabilityPill(
    label: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) Color(0xFF1A2621) else Color(0xFF261D1A),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) Color(0xFF2ED573).copy(alpha = 0.4f) else Color(0xFFFF4757).copy(alpha = 0.4f)
        ),
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color(0xFF2ED573) else Color(0xFFFF6B6B),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color(0xFF2ED573) else Color(0xFFFF6B6B),
                maxLines = 1
            )
        }
    }
}

