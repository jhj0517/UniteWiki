package com.unitewikiapp.unitewiki.datas

data class PokemonSearchData(val ic_pokemon:String? = "", val pokemon_name:String? = "",
                             var letter:String = "", val childList: ArrayList<PokemonSearchData> = arrayListOf())
{} //ic_pokemon is image URL sourced from Firebase.
