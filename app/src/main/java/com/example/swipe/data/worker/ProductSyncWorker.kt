package com.example.swipe.data.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.swipe.data.local.ProductDao
import com.example.swipe.data.remote.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val apiService: ApiService by inject()
    private val productDao: ProductDao by inject()
    override suspend fun doWork(): Result {
        val offlineProducts = productDao.getAllOfflineProducts()
        if (offlineProducts.isEmpty()) return Result.success()
        offlineProducts.forEach { offlineProduct ->
            try {
                val nameBody = offlineProduct.productName.toRequestBody("text/plain".toMediaTypeOrNull())
                val typeBody = offlineProduct.productType.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody = offlineProduct.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val taxBody = offlineProduct.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val imageUri = offlineProduct.imageUri?.let { Uri.parse(it) }
                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    applicationContext.contentResolver.openInputStream(imageUri)?.use {
                        val bytes = it.readBytes()
                        val reqFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("files[]", "image.jpg", reqFile)
                    }
                }
                val imageParts = if (imagePart != null) listOf(imagePart) else null
                val response = apiService.addProduct(nameBody, typeBody, priceBody, taxBody, imageParts)
                if (response.success) {
                    val myUpload = productDao.getMyUploads().first().find {
                        it.productName == offlineProduct.productName && !it.isSynced
                    }
                    myUpload?.let {
                        productDao.updateMyUpload(it.copy(isSynced = true))
                    }
                    productDao.deleteOfflineProduct(offlineProduct)
                }
            } catch (e: Exception) {
                return Result.retry()
            }
        }
        return Result.success()
    }
}