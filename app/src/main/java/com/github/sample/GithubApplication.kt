package com.github.sample

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.github.sample.webservice.APIService
import com.github.sample.webservice.GITConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class GithubApplication : Application() {

    companion object {
        lateinit var service: APIService
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
           Timber.d(it)
        }).setLevel(HttpLoggingInterceptor.Level.BASIC)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(GITConstants.GIT_HOST_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(APIService::class.java)

        sharedPreferences =  getSharedPreferences("GitHub_Sample", Context.MODE_PRIVATE)
    }
}