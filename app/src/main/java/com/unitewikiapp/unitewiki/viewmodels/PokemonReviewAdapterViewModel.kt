package com.unitewikiapp.unitewiki.viewmodels

import android.text.format.DateUtils
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.datas.localized
import com.unitewikiapp.unitewiki.utils.LocaleStore

class PokemonReviewAdapterViewModel (reviewItems:PokemonReviewsData, localeStore: LocaleStore) {

    private val items = reviewItems
    private val locale = localeStore.locale

    val userName:String?
        get() = items.userName
    val writing:String
        get() = items.writing
    val indicatorRatingbarRating:Float
        get() = items.rating.toFloat()
    val timeline:CharSequence?
        get() = DateUtils.getRelativeTimeSpanString(items.time!!, now, DateUtils.MINUTE_IN_MILLIS)
    val likes:String
        get() = items.likes.size.toString()
    val pokemon:String
        get() = items.pokemon!!.localized(currentLocale = locale!!)

    companion object {
        val now = System.currentTimeMillis()
    }

}