package com.unitewikiapp.unitewiki.datas

import com.google.firebase.database.FirebaseDatabase
import com.unitewikiapp.unitewiki.di.RealTimeDataBase
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RankRepository @Inject constructor(
    private val rtdb:FirebaseDatabase,
    private val localeStore: LocaleStore
    ) : RealTimeDataBase {
// Info repo 에서 뽑은 다음 그걸 viewmodel 에서 활용하는 식으로 가자.
    val locale = localeStore.findLocale()

    suspend fun fetchRankingData(type: String): Response<ArrayList<PokemonRankData>> {
        val ranks = try {
            rtdb.reference.child(Constants.POKEMON_INFO).get().await()
        } catch(e:Exception){
            return Response.Failure(e)
        }
        val ranksList = ArrayList<PokemonRankData>()
        ranks.children.forEach { snap->
            val data = snap.getValue(PokemonInfoData::class.java)!!
            val rank = PokemonRankData(
                ic_pokemon = data.ic_pokemon,
                pokemon_name = data.pokemon_name.ko,
                type = data.type
            )
            if (type == data.type){
                ranksList.add(rank)
            }
        }
        return Response.Success(ranksList)
    }
}