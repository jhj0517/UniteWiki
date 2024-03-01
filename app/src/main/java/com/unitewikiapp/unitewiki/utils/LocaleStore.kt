package com.unitewikiapp.unitewiki.utils

import android.content.Context

class LocaleStore(private val context: Context) {

    private val prefManager: PreferenceManager = PreferenceManager()

    private var _locale: String? = null
    val locale get() = _locale

    init {
        findLocale()
    }

    fun findLocale() {
        val currentLocale = prefManager.getString(context,"locale")
        return when (currentLocale) {
            null -> _locale = context.resources.configuration.locales.get(0).toString()
            "ko_kr" -> _locale = "ko"
            else -> _locale = "en"
        }
    }
}