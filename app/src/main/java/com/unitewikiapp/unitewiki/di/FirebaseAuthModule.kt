package com.unitewikiapp.unitewiki.di

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseAuthModule {

    @Singleton
    @Provides
    fun provideGSI(@ApplicationContext context: Context): GoogleSignInClient {
        return FirebaseAuthManager.initiateGSI(context)
    }

    @Singleton
    @Provides
    fun provideAuthUI(): AuthUI {
        return FirebaseAuthManager.initiateAuthUI()
    }

    @Provides
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuthManager.initiateAuth()
    }
}