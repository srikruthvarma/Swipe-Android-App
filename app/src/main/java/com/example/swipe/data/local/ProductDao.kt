package com.example.swipe.data.local
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface ProductDao {
    @Insert
    suspend fun insertOfflineProduct(product: OfflineProduct)
    @Query("SELECT * FROM offline_products")
    suspend fun getAllOfflineProducts(): List<OfflineProduct>
    @Delete
    suspend fun deleteOfflineProduct(product: OfflineProduct)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyUpload(upload: MyUpload)
    @Update
    suspend fun updateMyUpload(upload: MyUpload)
    @Query("SELECT * FROM my_uploads ORDER BY id DESC")
    fun getMyUploads(): Flow<List<MyUpload>>
}
