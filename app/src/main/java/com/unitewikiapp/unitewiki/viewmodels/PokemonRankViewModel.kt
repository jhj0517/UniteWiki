package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitewikiapp.unitewiki.datas.PokemonRankData
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.datas.RankRepository
import com.unitewikiapp.unitewiki.datas.ReviewRepository
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PokemonRankViewModel @Inject constructor(
    private val repository: RankRepository,
    private val reviewRepository: ReviewRepository
    )
    : ViewModel(){

    var rankList:MutableLiveData<ArrayList<PokemonRankData>> = MutableLiveData<ArrayList<PokemonRankData>>()

    fun fetchRanks(type:String){
        viewModelScope.launch {
            val myList = arrayListOf<PokemonRankData>()
            val res = repository.fetchRankingData(type)
            if(res is Response.Success){
                val data = res.data
                myList.addAll(data)
            } else {
                Log.d("RankViewModel Error","Firebase Error")
            }
            myList.forEach { setRating(it) }
            rankList.value = myList
        }
    }

    private suspend fun setRating(data:PokemonRankData){
        val reviewRef = reviewRepository.getReviewRef(data.pokemon_name!!)
        if(reviewRef is Response.Success){
            val snapshot = reviewRef.data.get().await()
            var ratingSum = 0.00f
            val size = snapshot.childrenCount.toInt()
            for ((index,ds) in snapshot.children.withIndex()) {
                val data = ds.getValue(PokemonReviewsData::class.java)!!
                ratingSum += data.rating
            }
            if(snapshot.childrenCount>0){
                val average = Math.round((ratingSum / size) * 10) /10.toFloat()
                data.average_rating = average
            }
        }
    }

}
