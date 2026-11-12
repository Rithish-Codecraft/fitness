package com.pulseforge.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.updater.UpdateManifest
import com.pulseforge.app.core.updater.UpdateState

@Composable
fun UpdateDialog(
    updateState: UpdateState,
    onStartDownload: (UpdateManifest) -> Unit,
    onInstallApk: (String) -> Unit,
    onDismiss: () -> Unit
) {
    when (updateState) {
        is UpdateState.UpdateAvailable -> {
            val manifest = updateState.manifest
            AlertDialog(
                onDismissRequest = { if (!manifest.isMandatory) onDismiss() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "New Update Available!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Version ${manifest.versionName} (Build ${manifest.versionCode})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Release Notes:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = manifest.releaseNotes.ifBlank { "General improvements and optimizations." },
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        if (manifest.fileSizeBytes > 0) {
                            val mb = manifest.fileSizeBytes / (1024.0 * 1024.0)
                            Text(
                                text = "Package Size: ${String.format("%.1f", mb)} MB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { onStartDownload(manifest) }) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download & Update")
                    }
                },
                dismissButton = {
                    if (!manifest.isMandatory) {
                        TextButton(onClick = onDismiss) {
                            Text("Later")
                        }
                    }
                }
            )
        }

        is UpdateState.Downloading -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Downloading Update...") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LinearProgressIndicator(
                            progress = (updateState.progressPercent / 100f).coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${updateState.progressPercent}%", fontWeight = FontWeight.Bold)
                            val downloadedMb = updateState.bytesDownloaded / (1024.0 * 1024.0)
                            val totalMb = updateState.totalBytes / (1024.0 * 1024.0)
                            Text("${String.format("%.1f", downloadedMb)} / ${String.format("%.1f", totalMb)} MB", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {}
            )
        }

        is UpdateState.ReadyToInstall -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Update Ready to Install") },
                text = {
                    Text("The latest APK has been downloaded successfully. Tap Install to apply the update.")
                },
                confirmButton = {
                    Button(onClick = { onInstallApk(updateState.apkPath) }) {
                        Text("Install Now")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                }
            )
        }

        is UpdateState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Update Check") },
                text = { Text(updateState.message) },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            )
        }

        is UpdateState.UpToDate -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Up to Date") },
                text = { Text("You are already using the latest version of PulseForge AI (${com.pulseforge.app.BuildConfig.VERSION_NAME}).") },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            )
        }

        else -> {}
    }
}

