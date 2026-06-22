package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val countryCode: String, // e.g. "US", "DE", "NL", "JP", "RU"
    val protocol: String, // e.g. "VLESS", "VMESS", "Trojan", "Shadowsocks", "Hysteria 2", "WireGuard"
    val address: String,
    val port: Int,
    val security: String = "Reality", // "Reality", "TLS", "None"
    val uuid: String = "",
    val path: String = "", // e.g. /grpc-service or WS path
    val sni: String = "",
    val pingMs: Int = -1,
    val isFavorite: Boolean = false,
    val customConfig: String = ""
)

@Entity(tableName = "vpn_subscriptions")
data class VpnSubscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val type: String, // "URL", "Base64", "Clash", "Sing-box"
    val autoUpdateHours: Int = 24,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "analytics_points")
data class AnalyticsPoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val downloadSpeedMbps: Float,
    val uploadSpeedMbps: Float,
    val pingMs: Int
)

@Entity(tableName = "vpn_logs")
data class VpnLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String, // "INFO", "WARN", "ERROR", "DEBUG"
    val message: String
)

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
