package com.unitewikiapp.unitewiki.di

import android.content.Context
import com.unitewikiapp.unitewiki.utils.LocaleStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppBaseModule {
    @Provides
    fun provideLocaleStore(@ApplicationContext context: Context): LocaleStore {
        return LocaleStore(context)
    }
}