package com.unitewikiapp.unitewiki.datas

data class PokemonInfoData(val ic_pokemon:String? = "", val pokemon_name:String? = "", val review_count:Long = 0,
                           val skill1:String = "", val skill1_name:String="", val skill1_cooltime:String="", val skill1_description:String= "",
                           val skill2:String = "", val skill2_name:String="", val skill2_cooltime:String="", val skill2_description:String= "",
                           val skill3:String = "", val skill3_name:String="", val skill3_cooltime:String="", val skill3_description:String= "",
                           val skill4:String = "", val skill4_name:String="", val skill4_cooltime:String="", val skill4_description:String= "",
                           val skill_unite:String = "", val skill_unite_name:String="", val skill_unite_cooltime:String="", val skill_unite_description:String= "",
                           val skillset1_level:String="", val skillset2_level:String="",
                           var skillset1_firstrate:Int = 0, var skillset2_firstrate:Int=0
                            )
{} //The strings of skills are image URLs sourced from Firebase.
