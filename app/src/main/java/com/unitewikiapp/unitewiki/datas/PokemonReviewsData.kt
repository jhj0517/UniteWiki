package com.unitewikiapp.unitewiki.datas

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken

data class PokemonReviewsData(
    @Expose val pokemon:LocaleField?=LocaleField(),
    @Expose val uid:String?="",
    @Expose val userName:String? = "",
    @Expose val writing:String ="",
    @Expose val rating:Int = 0,
    @Expose val selectedSkills:String="",
    @Expose val time:Long? = 0L,
    @Expose var likes:HashMap<String,Boolean> = hashMapOf(),
    @Expose var edited:Boolean = false,
    @Expose var reported:Int = 0,
    var isLiked:Boolean? = false,
    ) {
    fun toJson(): String{
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(this)
    }

    fun toMap(): Map<String, Any?>{
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val json = toJson()
        val type = object : TypeToken<Map<String, Any?>>() {}.type
        return gson.fromJson(json, type)
    }
}