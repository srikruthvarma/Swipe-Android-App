package com.example.swipe.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import com.example.swipe.data.local.MyUpload
import com.example.swipe.data.local.OfflineProduct
import com.example.swipe.data.local.ProductDao
import com.example.swipe.data.model.AddProductResponse
import com.example.swipe.data.model.Product
import com.example.swipe.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

interface ProductRepository {
    suspend fun getProducts(context: Context): Result<List<Product>>
    fun getMyUploads(): Flow<List<MyUpload>>
    suspend fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: Uri?,
        context: Context
    ): Result<AddProductResponse>
}

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun getProducts(context: Context): Result<List<Product>> {
        if (!isNetworkAvailable(context)) {
            return Result.failure(Exception("Offline"))
        }
        return try {
            Result.success(apiService.getProducts())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMyUploads(): Flow<List<MyUpload>> {
        return productDao.getMyUploads()
    }

    override suspend fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: Uri?,
        context: Context
    ): Result<AddProductResponse> = withContext(Dispatchers.IO) {
        val newUpload = MyUpload(
            productName = productName,
            productType = productType,
            price = price.toDoubleOrNull() ?: 0.0,
            tax = tax.toDoubleOrNull() ?: 0.0,
            imageUri = imageUri?.toString(),
            isSynced = false
        )
        productDao.insertMyUpload(newUpload)
        if (!isNetworkAvailable(context)) {
            val offlineProduct = OfflineProduct(
                productName = productName,
                productType = productType,
                price = price.toDoubleOrNull() ?: 0.0,
                tax = tax.toDoubleOrNull() ?: 0.0,
                imageUri = imageUri?.toString()
            )
            productDao.insertOfflineProduct(offlineProduct)
            return@withContext Result.failure(Exception("Offline. Product saved locally."))
        }
        return@withContext try {
            val nameBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = productType.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val taxBody = tax.toRequestBody("text/plain".toMediaTypeOrNull())
            var imagePart: MultipartBody.Part? = null
            if (imageUri != null) {
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("files[]", "image.jpg", requestFile)
                }
            }
            val imageParts = if (imagePart != null) listOf(imagePart) else null
            val response = apiService.addProduct(nameBody, typeBody, priceBody, taxBody, imageParts)
            val myUpload = productDao.getMyUploads().first().find { it.productName == productName && !it.isSynced }
            myUpload?.let {
                productDao.updateMyUpload(it.copy(isSynced = true))
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }
}