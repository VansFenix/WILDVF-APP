package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VpnRepository(private val vpnDao: VpnDao) {
    
    val servers: Flow<List<VpnServer>> = vpnDao.getServers()
    
    val subscriptions: Flow<List<VpnSubscription>> = vpnDao.getSubscriptions()
    
    val analytics: Flow<List<AnalyticsPoint>> = vpnDao.getAnalytics()
    
    val logs: Flow<List<VpnLog>> = vpnDao.getLogs()
    
    val settings: Flow<Map<String, String>> = vpnDao.getSettingsFlow().map { list ->
        list.associate { it.key to it.value }
    }

    suspend fun insertServer(server: VpnServer) {
        vpnDao.insertServer(server)
    }

    suspend fun updateServer(server: VpnServer) {
        vpnDao.updateServer(server)
    }

    suspend fun deleteServer(id: Int) {
        vpnDao.deleteServer(id)
    }

    suspend fun updateServerPing(id: Int, pingMs: Int) {
        vpnDao.updateServerPing(id, pingMs)
    }

    suspend fun insertSubscription(sub: VpnSubscription) {
        vpnDao.insertSubscription(sub)
    }

    suspend fun deleteSubscription(id: Int) {
        vpnDao.deleteSubscription(id)
    }

    suspend fun insertAnalyticsPoint(point: AnalyticsPoint) {
        vpnDao.insertAnalyticsPoint(point)
    }

    suspend fun clearAnalytics() {
        vpnDao.clearAnalytics()
    }

    suspend fun insertLog(level: String, message: String) {
        vpnDao.insertLog(VpnLog(level = level, message = message))
    }

    suspend fun clearLogs() {
        vpnDao.clearLogs()
    }

    suspend fun updateSetting(key: String, value: String) {
        vpnDao.insertSetting(AppSetting(key, value))
    }

    suspend fun getSettingValue(key: String): String? {
        return vpnDao.getSetting(key)?.value
    }
}
