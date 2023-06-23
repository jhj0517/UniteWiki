package com.unitewikiapp.unitewiki.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.unitewikiapp.unitewiki.utils.LocaleStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealTimeDataBaseModule {

    @Provides
    @Singleton
    fun provideRealTimeDB(): FirebaseDatabase {
        return RealTimeDataBase.initiate()
    }

    @Provides
    fun provideLocaleStore(@ApplicationContext context: Context): LocaleStore {
        return LocaleStore(context)
    }

}