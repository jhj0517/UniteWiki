package com.unitewikiapp.unitewiki.datas

data class PokemonReviewsData(
    val pokemon:String? = "", //image URL sourced from Firebase.
    val uid:String?="",
    val userName:String? = "",
    val writing:String ="",
    val rating:Int = 0,
    val firstSkillSetRate:Int = -1,
    val secondSkillSetRate:Int = -1,
    val time:Long? = 0L,
    var likesNumber:Long = 0,
    var likes:HashMap<String,Boolean> = hashMapOf(),
    var edited:Boolean = false,
    var reported:Int = 0,
    var isLiked:Boolean? = false,
    ) {}