package com.example.swipe.data.model

data class UiProduct(
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val image: String?,
    val isPendingSync: Boolean = false
)