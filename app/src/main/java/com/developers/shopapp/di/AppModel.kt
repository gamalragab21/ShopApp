package com.developers.shopapp.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.developers.shopapp.qualifiers.IOThread
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.qualifiers.Token
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Constants.TAG
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModel {


    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context

    // TODO: 11/8/2021  For implementation MainDispatcher

    @MainThread
    @Singleton
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main


    // TODO: 11/8/2021  For implementation IODispatcher

    @IOThread
    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO


    // TODO: 11/8/2021  For implementation Glide
    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

    )
    @Provides
    @Singleton
    fun dataStoreManager(@ApplicationContext appContext: Context): DataStoreManager =
        DataStoreManager(appContext)

    // TODO: 11/8/2021  For implementation AppDatabase

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext appContext: Context): JobDataBase {
//        return Room.databaseBuilder(
//            appContext,
//            JobDataBase::class.java,
//            "job_DB"
//        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
//            .build()
//    }




}