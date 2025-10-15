package com.example.swipe.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("image") val image: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("tax") val tax: Double
)

data class AddProductResponse(
    @SerializedName("message") val message: String,
    @SerializedName("product_details") val productDetails: Product,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("success") val success: Boolean
)