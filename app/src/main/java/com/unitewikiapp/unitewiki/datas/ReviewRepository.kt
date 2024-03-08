package com.unitewikiapp.unitewiki.datas

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.unitewikiapp.unitewiki.di.RealTimeDataBase
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val rtdb: FirebaseDatabase,
    private val localeStore: LocaleStore,
): RealTimeDataBase {

    val locale = localeStore.findLocale()

    suspend fun fetchReviewSnapshot(): Response<DataSnapshot> {
        return try {
            val info = rtdb.reference.child(Constants.POKEMON_REVIEW+"_"+localeStore.locale!!).get().await()
            Response.Success(info)
        } catch(e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun fetchReviewReference(review: PokemonReviewsData): Response<DatabaseReference>{
        return try {
            val ref = rtdb.reference.child(Constants.POKEMON_REVIEW+"_"+localeStore.locale!!)
                .child(review.pokemon!!.en).child(review.uid!!)
            Response.Success(ref)
        } catch (e:Exception){
            Response.Failure(e)
        }
    }
}