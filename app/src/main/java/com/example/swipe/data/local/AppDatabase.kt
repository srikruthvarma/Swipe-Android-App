package com.example.swipe.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [OfflineProduct::class, MyUpload::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
