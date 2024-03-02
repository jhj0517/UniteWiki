package com.unitewikiapp.unitewiki.datas

data class PokemonReviewsData(
    val pokemon:LocaleField?=LocaleField(),
    val uid:String?="",
    val userName:String? = "",
    val writing:String ="",
    val rating:Int = 0,
    val selectedSkills:String="",
    val time:Long? = 0L,
    var likes:HashMap<String,Boolean> = hashMapOf(),
    var edited:Boolean = false,
    var reported:Int = 0,
    var isLiked:Boolean? = false,
    ) {}