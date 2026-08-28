package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WatchHistoryEntity::class,
        FavoriteEntity::class,
        DownloadEntity::class,
        UserSubscriptionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DonghuaDatabase : RoomDatabase() {
    abstract fun donghuaDao(): DonghuaDao

    companion object {
        @Volatile
        private var INSTANCE: DonghuaDatabase? = null

        fun getDatabase(context: Context): DonghuaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonghuaDatabase::class.java,
                    "donghua_stream.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
