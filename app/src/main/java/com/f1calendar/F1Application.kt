package com.f1calendar

import android.app.Application
import androidx.room.Room
import com.f1calendar.data.local.F1Database
import com.f1calendar.data.remote.F1ApiService
import com.f1calendar.data.repository.F1Repository
import com.f1calendar.util.AppLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class F1Application : Application() {
    lateinit var repository: F1Repository
        private set

    override fun onCreate() {
        super.onCreate()

        AppLogger.i("F1Application", "========================================")
        AppLogger.i("F1Application", "App starting - F1 Calendar")
        AppLogger.i("F1Application", "========================================")

        // Initialize Room Database
        AppLogger.d("F1Application", "Initializing Room database")
        val database = Room.databaseBuilder(
            applicationContext,
            F1Database::class.java,
            "f1_database"
        ).build()

        // Initialize Retrofit
        val apiBaseUrl = "https://api.jolpi.ca/ergast/"
        AppLogger.d("F1Application", "Initializing Retrofit with base URL: $apiBaseUrl")

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(F1ApiService::class.java)

        // Initialize Repository
        AppLogger.d("F1Application", "Initializing Repository")
        repository = F1Repository(apiService, database.raceDao())

        AppLogger.i("F1Application", "App initialization complete")
    }
}
