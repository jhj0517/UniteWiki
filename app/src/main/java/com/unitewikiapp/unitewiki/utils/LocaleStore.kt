package com.unitewikiapp.unitewiki.utils

import android.content.Context

class LocaleStore(private val context: Context) {

    private val prefManager: PreferenceManager = PreferenceManager()

    fun findLocale():String {
        val currentLocale = prefManager.getString(context,"locale")
        return when (currentLocale) {
            "ko_KR"-> ""
            "ko_kr" -> ""
            else -> "EN"
        }
    }

}