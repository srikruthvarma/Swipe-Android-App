package com.example.swipe.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "my_uploads")
data class MyUpload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val imageUri: String?,
    val isSynced: Boolean = false
)