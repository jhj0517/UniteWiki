package com.unitewikiapp.unitewiki.datas

import java.text.SimpleDateFormat
import java.util.*

data class PokemonReviewWritingData (
    val pokemon:String="", //image URL sourced from Firebase.
    val firstSkillSetRate:Int=-1,
    val secondSkillSetRate:Int=-1,
    val writing:String="",
    val likesNumber:Int=0,
    val time:Long=timeFromSdf,
    val rating:Int=0,
    val userName:String="",
    val uid:String=""
){
    companion object{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val sdft = sdf.format(Date())
        val timeFromSdf:Long = sdf.parse(sdft).time
    }
}
