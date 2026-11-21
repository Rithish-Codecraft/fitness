package com.pulseforge.app.presentation.policy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import com.pulseforge.app.core.policy.AccessCategory
import com.pulseforge.app.core.policy.AndroidPolicyState
import com.pulseforge.app.core.policy.PermissionAccessItem
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPolicyScreen(
    policyState: AndroidPolicyState,
    onAcceptPolicy: () -> Unit,
    onRevokePolicy: () -> Unit,
    onGrantAllRuntimePermissions: () -> Unit,
    onGrantSingleItem: (PermissionAccessItem) -> Unit,
    onOpenAppInfoSettings: () -> Unit,
    onOpenUsageAccessSettings: () -> Unit,
    onOpenRestrictedSettingsHelp: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Policy & Android Access",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. User Policy & Consent Card
            item {
                PulseCard(borderGlow = !policyState.isPolicyAccepted) {
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "User Policy",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                Text(
                                    text = "PulseForge User & Privacy Policy",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Policy v${policyState.policyVersion} • Effective ${policyState.effectiveDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (policyState.isPolicyAccepted) {
                                Color(0xFF2ED573).copy(alpha = 0.16f)
                            } else {
                                Color(0xFFFFA502).copy(alpha = 0.16f)
                            }
                        ) {
                            Text(
                                text = if (policyState.isPolicyAccepted) "Accepted" else "Review Needed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (policyState.isPolicyAccepted) Color(0xFF2ED573) else Color(0xFFFFA502),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Google Android & Health Connect Data Commitments",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val commitments = listOf(
                        "• On-device processing only; no third-party ad tracking.",
                        "• Health Connect (read-only) used solely for coaching.",
                        "• Runtime permissions can be revoked anytime in Settings.",
                        "• JSON backups in Documents/PulseForge AI belong to you."
                    )
                    commitments.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (policyState.isPolicyAccepted) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accepted (${policyState.acceptedTimestamp ?: "Active"})",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF2ED573),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            TextButton(onClick = onRevokePolicy) {
                                Text("Revoke", maxLines = 1, softWrap = false)
                            }
                        }
                    } else {
                        Button(
                            onClick = onAcceptPolicy,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Accept Policy (v${policyState.policyVersion})", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }

            // 2. Android 13+ Restricted Settings Card (support.google.com/android/answer/12623953)
            item {
                PulseCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Restricted Settings",
                            tint = Color(0xFFFFA502)
                        )
                        Column {
                            Text(
                                text = "Android Restricted Settings Guide",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Google Android Help • Answer 12623953",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val steps = listOf(
                        "1. Open App Info -> ⋮ (top-right) -> Allow restricted settings.",
                        "2. Open Usage Access -> enable PulseForge AI."
                    )
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenUsageAccessSettings,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Usage Access", fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                        OutlinedButton(
                            onClick = onOpenAppInfoSettings,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("App Info", fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = onOpenRestrictedSettingsHelp,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Read Google Android Policy (12623953)", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 3. Permissions & Special Access Header
            item {
                SectionHeader(
                    title = "Grant Android Permissions (${policyState.grantedCount}/${policyState.totalApplicableCount})",
                    subtitle = "Follows Android runtime & special app access guidelines"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onGrantAllRuntimePermissions,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Grant All Runtime Permissions")
                }
            }

            // 4. Individual Permission & Special Access Items
            items(policyState.accessItems, key = { it.id }) { item ->
                PermissionAccessRowCard(
                    item = item,
                    onActionClick = { onGrantSingleItem(item) },
                    onOpenAppInfoSettings = onOpenAppInfoSettings
                )
            }
        }
    }
}

@Composable
private fun PermissionAccessRowCard(
    item: PermissionAccessItem,
    onActionClick: () -> Unit,
    onOpenAppInfoSettings: () -> Unit = {}
) {
    val statusColor = if (item.isGranted) Color(0xFF2ED573) else Color(0xFFFF6B6B)
    val categoryLabel = when (item.category) {
        AccessCategory.RUNTIME -> "Runtime Permission"
        AccessCategory.SPECIAL_RESTRICTED -> "Special App Access"
        AccessCategory.HEALTH_CONNECT -> "Health Connect"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$categoryLabel • ${item.androidPermission}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (item.isGranted) "GRANTED" else "REQUIRED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = item.purpose,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.restrictionNote != null && !item.isGranted) {
                Text(
                    text = item.restrictionNote,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFA502)
                )
            }

            if (!item.isGranted) {
                if (item.id == "usage_access") {
                    OutlinedButton(
                        onClick = onOpenAppInfoSettings,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open App Info", maxLines = 1, softWrap = false)
                    }
                }
                OutlinedButton(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (item.category) {
                            AccessCategory.RUNTIME -> "Grant"
                            AccessCategory.HEALTH_CONNECT -> "Grant Health Connect"
                            AccessCategory.SPECIAL_RESTRICTED -> "Open Settings"
                        },
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
