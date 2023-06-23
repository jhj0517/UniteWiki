package com.unitewikiapp.unitewiki.viewmodels

import android.text.format.DateUtils
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData

class PokemonReviewAdapterViewModel (reviewItems:PokemonReviewsData) {

    private val items = reviewItems

    val userName:String?
        get() = items.userName
    val writing:String?
        get() = items.writing
    val indicatorRatingbarRating:Float
        get() = items.rating.toFloat()
    val timeline:CharSequence?
        get() = DateUtils.getRelativeTimeSpanString(items.time!!, now, DateUtils.MINUTE_IN_MILLIS)
    val likes:String
        get() = items.likesNumber.toString()
    val pokemon:String?
        get() = items.pokemon

    companion object {
        val now = System.currentTimeMillis()
    }

}