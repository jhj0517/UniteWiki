package com.unitewikiapp.unitewiki.viewmodels

import com.unitewikiapp.unitewiki.datas.SearchListParentData

class SearchListParentAdapterViewModel (Items: SearchListParentData) {
    private val items = Items

    val firstLetter:String
        get() = items.firstLetter

}