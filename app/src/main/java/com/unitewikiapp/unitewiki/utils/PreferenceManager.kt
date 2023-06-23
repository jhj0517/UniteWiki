package com.unitewikiapp.unitewiki.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {

    companion object {
        const val PREFERENCES_NAME = "shared_preference"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    /**
     *
     * Save String value
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setString(context: Context, key: String?, value: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     *
     * Save boolean value
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     *
     * Save int value
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setInt(context: Context, key: String?, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     *
     * Save long value
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setLong(context: Context, key: String?, value: Long) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    /**
     *
     *  Save float value
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setFloat(context: Context, key: String?, value: Float) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    /**
     *
     *  Load String value
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getString(context: Context, key: String?): String? {
        val prefs = getPreferences(context)
        val default = context.resources.configuration.locales.get(0).toString()
        return prefs.getString(key, default)
    }


    /**
     *
     * Load boolean value
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getBoolean(context: Context, key: String?): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /**
     *
     *  Load int value
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getInt(context: Context, key: String?): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /**
     *
     * Load long value
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getLong(context: Context, key: String?): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /**
     *
     * Load float value
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getFloat(context: Context, key: String?): Float {
        val prefs = getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     *
     * remove Key
     *
     * @param context
     *
     * @param key
     */
    fun removeKey(context: Context, key: String?) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.commit()
    }

    /**
     *
     * remove All data
     *
     * @param context
     */
    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.clear()
        edit.commit()
    }
}