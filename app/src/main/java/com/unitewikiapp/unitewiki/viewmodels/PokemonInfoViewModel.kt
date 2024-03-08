package com.unitewikiapp.unitewiki.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.librarydevloperjo.kcs.KCS
import com.unitewikiapp.unitewiki.datas.*
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonInfoViewModel @Inject constructor(
    private val repository: InfoRepository,
    private val localeStore: LocaleStore
): ViewModel() {

    private val _infoSnapshot = MutableLiveData<DataSnapshot?>()
    val infoSnapshot get() = _infoSnapshot

    private val _currentPokemon = MutableLiveData<PokemonInfoData>()
    val currentPokemon get() = _currentPokemon

    init {
        fetchInfoSnapshot()
    }

    private fun fetchInfoSnapshot(){
        viewModelScope.launch {
            val snapShot = repository.fetchInfoSnapshot()
            when (snapShot){
                is Response.Success -> {
                    _infoSnapshot.value = snapShot.data
                }
                else -> {
                    throw Exception("Failed to fetch data")
                }
            }
        }
    }

    fun getRankDataByType(type: String): ArrayList<PokemonRankData>{
        val ranksList = ArrayList<PokemonRankData>()
        infoSnapshot.value!!.children.forEach { snap->
            val data = snap.getValue(PokemonInfoData::class.java)!!
            val rank = PokemonRankData(
                ic_pokemon = data.ic_pokemon,
                pokemon_name = data.pokemon_name.localized(localeStore.locale!!),
                type = data.type
            )
            if (type == data.type){
                ranksList.add(rank)
            }
        }
        return ranksList
    }

    fun getList(): ArrayList<PokemonInfoData>{
        val list = ArrayList<PokemonInfoData>()
        infoSnapshot.value!!.children.forEach { snap->
            val data = snap.getValue(PokemonInfoData::class.java)!!
            list.add(data)
        }
        return list
    }

    fun getListByAlphabet(): ArrayList<SearchListParentData>{
        val list = getList()
        val groups = list.groupBy { it ->
            val name = it.pokemon_name.localized(localeStore.locale!!)
            when(localeStore.locale){
                "ko" -> KCS.getCho(name.substring(0,1))
                else -> name.substring(0,1)
            }
        }
        val listByAlphabet = ArrayList<SearchListParentData>()
        groups.forEach { key, value ->
            listByAlphabet.add(
                SearchListParentData(
                    firstLetter = key,
                    childList = ArrayList(value)
                )
            )
        }
        return listByAlphabet
    }

    fun setCurrentPokemon(name: String) {
        infoSnapshot.value!!.children.forEach { snap ->
            val data = snap.getValue(PokemonInfoData::class.java)!!
            if (data.pokemon_name.localized(localeStore.locale!!) == name){
                _currentPokemon.value = data
                return
            }
        }
    }

}