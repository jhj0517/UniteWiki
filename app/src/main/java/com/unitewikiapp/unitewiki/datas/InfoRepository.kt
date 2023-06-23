package com.unitewikiapp.unitewiki.datas

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.unitewikiapp.unitewiki.di.RealTimeDataBase
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InfoRepository @Inject constructor(
    private val rtdb: FirebaseDatabase,
    private val localeStore: LocaleStore
    ): RealTimeDataBase {

    private val locale = localeStore.findLocale()

    suspend fun getInfoData(pokemonName:String): PokemonInfoData {
        return try {
            val info:PokemonInfoData = rtdb.reference.child(Constants.POKEMON_INFO+locale).child(pokemonName).get().await()
                .getValue(PokemonInfoData::class.java)!!
            info
        }catch(e:Exception){
            PokemonInfoData()
        }
    }

    suspend fun getSearchDataRef(): Response<DatabaseReference> {
        return try {
            val ref = rtdb.reference.child(Constants.POKEMON_INFO+locale)
            Response.Success(ref)
        }catch(e:Exception){
            Response.Failure(e)
        }
    }
}