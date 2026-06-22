package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {
    // Servers
    @Query("SELECT * FROM vpn_servers ORDER BY isFavorite DESC, name ASC")
    fun getServers(): Flow<List<VpnServer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServer)

    @Update
    suspend fun updateServer(server: VpnServer)

    @Query("DELETE FROM vpn_servers WHERE id = :id")
    suspend fun deleteServer(id: Int)

    @Query("UPDATE vpn_servers SET pingMs = :pingMs WHERE id = :id")
    suspend fun updateServerPing(id: Int, pingMs: Int)

    // Subscriptions
    @Query("SELECT * FROM vpn_subscriptions ORDER BY id DESC")
    fun getSubscriptions(): Flow<List<VpnSubscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: VpnSubscription)

    @Query("DELETE FROM vpn_subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: Int)

    // Analytics
    @Query("SELECT * FROM analytics_points ORDER BY timestamp ASC LIMIT 50")
    fun getAnalytics(): Flow<List<AnalyticsPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticsPoint(point: AnalyticsPoint)

    @Query("DELETE FROM analytics_points")
    suspend fun clearAnalytics()

    // Logs
    @Query("SELECT * FROM vpn_logs ORDER BY timestamp DESC LIMIT 200")
    fun getLogs(): Flow<List<VpnLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VpnLog)

    @Query("DELETE FROM vpn_logs")
    suspend fun clearLogs()

    // Settings
    @Query("SELECT * FROM app_settings")
    fun getSettingsFlow(): Flow<List<AppSetting>>

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): AppSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSetting)
}

@Database(
    entities = [
        VpnServer::class,
        VpnSubscription::class,
        AnalyticsPoint::class,
        VpnLog::class,
        AppSetting::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VpnDatabase : RoomDatabase() {
    abstract fun vpnDao(): VpnDao

    companion object {
        @Volatile
        private var INSTANCE: VpnDatabase? = null

        fun getDatabase(context: Context): VpnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VpnDatabase::class.java,
                    "wildvf_vpn_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
