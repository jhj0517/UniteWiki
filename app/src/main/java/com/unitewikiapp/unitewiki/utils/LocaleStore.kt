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
        var currentLocale = context.resources.configuration.locales.get(0).toString().lowercase()
        currentLocale = if(currentLocale=="ko_kr") "ko" else currentLocale
        prefManager.setString(context,"locale",currentLocale)
        _locale = prefManager.getString(context,"locale")
    }
}