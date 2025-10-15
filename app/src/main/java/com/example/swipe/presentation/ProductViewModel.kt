package com.example.swipe.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.swipe.data.local.MyUpload
import com.example.swipe.data.model.Product
import com.example.swipe.data.repository.ProductRepository
import com.example.swipe.data.worker.ProductSyncWorker
import com.example.swipe.util.showProductAddedNotification
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

data class HomeUiState(
    val totalUploads: Int = 0,
    val pendingUploads: Int = 0,
    val recentUploads: List<MyUpload> = emptyList() // Added this property
)

class ProductViewModel(
    private val repository: ProductRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allProductsState = MutableStateFlow(ProductListUiState())
    val allProductsState: StateFlow<ProductListUiState> = _allProductsState.asStateFlow()

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _myUploadsState = MutableStateFlow<List<MyUpload>>(emptyList())
    val myUploadsState: StateFlow<List<MyUpload>> = _myUploadsState.asStateFlow()

    private val _postResult = MutableSharedFlow<String>()
    val postResult = _postResult.asSharedFlow()

    init {
        fetchProducts()

        viewModelScope.launch {
            repository.getMyUploads().collect { uploads ->
                _myUploadsState.value = uploads
                _homeState.update {
                    it.copy(
                        totalUploads = uploads.size,
                        pendingUploads = uploads.count { !it.isSynced },
                        recentUploads = uploads.take(3) // Logic to get the 3 most recent
                    )
                }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _allProductsState.update { it.copy(isLoading = true, error = null) }
            repository.getProducts(getApplication())
                .onSuccess { newProducts ->
                    _allProductsState.update { currentState ->
                        val filtered = if (currentState.searchQuery.isBlank()) newProducts else newProducts.filter { p -> p.productName.contains(currentState.searchQuery, true) }
                        currentState.copy(products = newProducts, filteredProducts = filtered, isLoading = false)
                    }
                }
                .onFailure { error ->
                    _allProductsState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _allProductsState.update {
            val filtered = if (query.isBlank()) it.products else it.products.filter { p -> p.productName.contains(query, true) }
            it.copy(searchQuery = query, filteredProducts = filtered)
        }
    }

    fun addProduct(name: String, type: String, price: String, tax: String, uri: Uri?) {
        viewModelScope.launch {
            val result = repository.addProduct(name, type, price, tax, uri, getApplication())
            result.onSuccess { response ->
                _postResult.emit(response.message)
                showProductAddedNotification(getApplication(), response.productDetails.productName)
                fetchProducts()
            }
            result.onFailure { error ->
                _postResult.emit(error.message ?: "An unknown error occurred")
                if (error.message?.contains("Offline") == true) {
                    val context = getApplication<Application>().applicationContext
                    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    val syncWorkRequest = OneTimeWorkRequestBuilder<ProductSyncWorker>().setConstraints(constraints).build()
                    WorkManager.getInstance(context).enqueue(syncWorkRequest)
                }
            }
        }
    }
}