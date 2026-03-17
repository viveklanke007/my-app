package com.aiearcoach.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "coaching_logs")
data class CoachingLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val transcription: String,
    val response: String,
    val situation: String,
    val emotion: String? = null,
    val speechSpeed: Float? = null
)

@Dao
interface CoachingDao {
    @Insert
    suspend fun insertLog(log: CoachingLog)

    @Query("SELECT * FROM coaching_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CoachingLog>>
}

@Database(entities = [CoachingLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coachingDao(): CoachingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coach_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
