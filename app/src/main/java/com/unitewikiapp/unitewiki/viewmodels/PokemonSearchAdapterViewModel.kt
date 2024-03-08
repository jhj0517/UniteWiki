package com.unitewikiapp.unitewiki.viewmodels

import com.unitewikiapp.unitewiki.datas.LocaleField
import com.unitewikiapp.unitewiki.datas.PokemonInfoData

class PokemonSearchAdapterViewModel (Items: PokemonInfoData) {
    private val items = Items

    val pokemonName:LocaleField
        get() = items.pokemon_name
    val icPokemon:String
        get() = items.ic_pokemon

}