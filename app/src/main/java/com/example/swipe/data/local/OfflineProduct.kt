package com.example.swipe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_products")
data class OfflineProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val imageUri: String?
)