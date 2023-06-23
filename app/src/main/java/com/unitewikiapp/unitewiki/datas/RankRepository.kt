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

    val locale = localeStore.findLocale()

    suspend fun fetchRankingData(type:String): Response<ArrayList<PokemonRankData>> {
        return try {
            val ranksList = ArrayList<PokemonRankData>()

            val ranks = rtdb.reference.child(Constants.POKEMON_RANKS+locale).child(type).get().await()
            ranks.children.forEach { snap->
                val data = snap.getValue(PokemonRankData::class.java)!!
                ranksList.add(data)
            }

            Response.Success(ranksList)
        }catch(e:Exception){
            Response.Failure(e)
        }
    }
}