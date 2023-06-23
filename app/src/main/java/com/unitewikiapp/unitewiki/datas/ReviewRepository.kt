package com.unitewikiapp.unitewiki.datas

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.unitewikiapp.unitewiki.di.RealTimeDataBase
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import javax.inject.Inject

const val REVIEWS_NUMBER_TO_SHOW = 3

class ReviewRepository @Inject constructor(
    private val rtdb: FirebaseDatabase,
    private val localeStore: LocaleStore,
): RealTimeDataBase {

    val locale = localeStore.findLocale()

    suspend fun getMyReviewRef(pokemonName:String, myUid:String): Response<DatabaseReference> {
        return try{
            val ref = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .child(myUid)
            Response.Success(ref)
        }catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getReviewRef(pokemonName:String): Response<DatabaseReference> {
        return try{
            val ref = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
            Response.Success(ref)
        }catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getQueryForLike(pokemonName:String, uid:String, uidOfWriter:String): Response<DatabaseReference> {
        return try{
            val query = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .child(uidOfWriter).child(Constants.QUERY_LIKES).child(uid)
            Response.Success(query)
        } catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getQueryForMyReview(pokemonName:String, uid:String): Response<DatabaseReference> {
        return try{
            val myReviewQuery = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .child(uid)
            Response.Success(myReviewQuery)
        } catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getQueryForReport(pokemonName:String, uid:String, uidOfWriter:String): Response<DatabaseReference> {
        return try{
            val reportQuery = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .child(uidOfWriter).child(Constants.QUERY_REPORTERS).child(uid)
            Response.Success(reportQuery)
        } catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getQueryInfo(pokemonName:String, orderQuery:String): Response<Query> {
        return try{
            val query = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .orderByChild(orderQuery).limitToLast(REVIEWS_NUMBER_TO_SHOW)
            Response.Success(query)
        }catch (e:Exception){
            Response.Failure(e)
        }
    }

    suspend fun getQuery(pokemonName:String, orderQuery:String): Response<Query> {
        return try{
            val query = rtdb.reference.child(Constants.POKEMON_REVIEW+locale).child(pokemonName)
                .orderByChild(orderQuery)
            Response.Success(query)
        }catch (e:Exception){
            Response.Failure(e)
        }
    }

    companion object {
        private const val REVIEW_PAGE_SIZE = 20
    }
}