package com.unitewikiapp.unitewiki.datas

data class PokemonInfoData(
    val ic_pokemon: String="",
    val pokemon_name: LocaleField = LocaleField(),
    val review_count: Long=0,
    val skills: List<Skill>?= listOf(),
    val type: String= "",
)

data class Skill(
    val ic: String="",
    val name: LocaleField= LocaleField(),
    val cooltime: String="",
    val description: LocaleField=LocaleField(),
    val min_level: String="",
)