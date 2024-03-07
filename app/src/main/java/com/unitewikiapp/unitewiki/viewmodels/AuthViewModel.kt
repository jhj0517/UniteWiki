package com.unitewikiapp.unitewiki.viewmodels

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.unitewikiapp.unitewiki.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


const val signInCode:Int = 100

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val auth: FirebaseAuth,
    private val authUI:AuthUI
):ViewModel(){

    private val _currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)
    val currentUser get() = _currentUser

    fun signIn(context:Activity){
        val signInIntent = googleSignInClient.signInIntent
        context.startActivityForResult(signInIntent, signInCode)
    }

    fun getAuthUI():AuthUI{
        return authUI
    }

    fun addAuthListener(){
        auth.addAuthStateListener {
            when(it.currentUser){
                null-> currentUser.value = null
                else -> {currentUser.value = it.currentUser
                        Log.d("Firebase Login","Signed-In!")}
            }
        }
    }

    fun getResultOnActivity(requestCode: Int, resultCode: Int, data: Intent?, context: Activity) {
        if (requestCode == signInCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!,context)
            } catch (e: ApiException) {
                Log.i("Firebase Login Error", "$e")
            }
        }
    }

   fun firebaseAuthWithGoogle(idToken: String,context: Activity) {
        var pd = ProgressDialog(context)
        pd.setMessage(context.resources.getString(R.string.loginisprogressing))
        pd.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    pd.dismiss()
                }else{
                    Toast.makeText(context,"google sign in failed", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e ->
                Log.i("google sign in failed", "$e")
            }
   }

}