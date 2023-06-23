package com.unitewikiapp.unitewiki.di

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unitewikiapp.unitewiki.R

abstract class FirebaseAuthManager {
    companion object {
        fun initiateGSI(context: Context): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(context, gso)
        }

        fun initiateAuth(): FirebaseAuth {
            return Firebase.auth
        }

        fun initiateAuthUI():AuthUI{
            return AuthUI.getInstance()
        }
    }
}