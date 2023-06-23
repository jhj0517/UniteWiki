package com.unitewikiapp.unitewiki.viewmodels

import com.unitewikiapp.unitewiki.datas.PokemonRankData

class PokemonRankAdapterViewModel (rankItems: PokemonRankData) {
    private val items = rankItems

    val pokemonName:String?
        get() = items.pokemon_name
    val icPokemon:String?
        get() = items.ic_pokemon
    val averageRatingRatingBar:Float
        get() = items.average_rating
    val averageRating:String?
        get() = items.average_rating.toString()

}