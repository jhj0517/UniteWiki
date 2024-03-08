package com.unitewikiapp.unitewiki

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.unitewikiapp.unitewiki.utils.ContextWrapper
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity () {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val loadingJob = launch {
                delay(700L) //Splash screen time
                setTheme(R.style.Theme_UniteWiki)
            }
            loadingJob.join()

            val afterLoading = launch {
                setContentView(R.layout.activity_main)
            }
            afterLoading.join()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        authViewModel.addAuthListener()
        return super.onCreateView(name, context, attrs)
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeStore = LocaleStore(newBase!!)
        val locale = localeStore.locale
        super.attachBaseContext(ContextWrapper.wrap(newBase, Locale(locale!!)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authViewModel.getResultOnActivity(requestCode,resultCode,data,this)
    }
}