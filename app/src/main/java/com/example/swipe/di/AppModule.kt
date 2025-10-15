package com.example.swipe.di

import androidx.room.Room
import com.example.swipe.data.local.AppDatabase
import com.example.swipe.data.remote.ApiService
import com.example.swipe.data.repository.ProductRepository
import com.example.swipe.data.repository.ProductRepositoryImpl
import com.example.swipe.presentation.ProductViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                    .build()
            )
            .build()
            .create(ApiService::class.java)
    }

    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "swipe_database"
        )
            .fallbackToDestructiveMigration() // This prevents crashes on DB version changes
            .build()
    }

    single { get<AppDatabase>().productDao() }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    viewModel { ProductViewModel(get(), androidApplication()) }
}