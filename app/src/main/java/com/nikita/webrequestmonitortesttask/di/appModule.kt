// appModule.kt
package com.nikita.webrequestmonitortesttask.di

import com.nikita.webrequestmonitortesttask.data.local.AppDatabase
import com.nikita.webrequestmonitortesttask.repository.RequestRepository
import com.nikita.webrequestmonitortesttask.utils.ContextUtils
import com.nikita.webrequestmonitortesttask.viewmodel.RequestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val AppModule = module {

    single { AppDatabase.getDatabase(get()) }

    single { get<AppDatabase>().requestDao() }

    single { RequestRepository(get()) }

    viewModel { RequestViewModel(get(), get()) }

    single { ContextUtils(get()) }

    single { provideRetrofit() }
}

fun provideRetrofit(): Retrofit {
    Timber.d("Creating Retrofit instance")
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.google.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    Timber.d("Retrofit created with baseUrl: https://www.google.com/")
    return retrofit
}