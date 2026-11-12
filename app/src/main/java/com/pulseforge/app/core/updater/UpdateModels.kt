package com.pulseforge.app.core.updater

import com.google.gson.annotations.SerializedName

data class UpdateManifest(
    @SerializedName("versionCode") val versionCode: Int = 1,
    @SerializedName("versionName") val versionName: String = "1.0.0",
    @SerializedName("apkUrl") val apkUrl: String = "",
    @SerializedName("releaseNotes") val releaseNotes: String = "",
    @SerializedName("isMandatory") val isMandatory: Boolean = false,
    @SerializedName("fileSizeBytes") val fileSizeBytes: Long = 0L,
    @SerializedName("sha256") val sha256: String = ""
)

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class UpdateAvailable(val manifest: UpdateManifest) : UpdateState()
    object UpToDate : UpdateState()
    data class Downloading(val progressPercent: Int, val bytesDownloaded: Long, val totalBytes: Long) : UpdateState()
    data class ReadyToInstall(val apkPath: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

