package com.unitewikiapp.unitewiki

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.unitewikiapp.unitewiki.utils.ContextWrapper
import com.unitewikiapp.unitewiki.utils.PreferenceManager
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity () {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val loadingJob = launch {
                delay(700L) //Splash screen time
                setTheme(R.style.Theme_UniteWiki)
            }
            loadingJob.join()

            val afterloading = launch {
                setContentView(R.layout.activity_main)
            }
            afterloading.join()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        loginViewModel.addAuthListener()
        return super.onCreateView(name, context, attrs)
    }

    private val prefManager:PreferenceManager = PreferenceManager()

    override fun attachBaseContext(newBase: Context?) {
        var localelang = prefManager.getString(newBase!!,"locale")
        when(localelang){
            "ko_KR" -> {}
            "ko_kr" -> {}
            else -> localelang = "en"
        }
        val locale = Locale(localelang)
        super.attachBaseContext(ContextWrapper.wrap(newBase,locale))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.getResultOnActivity(requestCode,resultCode,data,this)
    }
}