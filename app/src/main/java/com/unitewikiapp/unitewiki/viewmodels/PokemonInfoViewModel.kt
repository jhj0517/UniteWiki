package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.unitewikiapp.unitewiki.datas.InfoRepository
import com.unitewikiapp.unitewiki.datas.PokemonInfoData
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.datas.ReviewRepository
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PokemonInfoViewModel @Inject constructor(
    private val repository: InfoRepository,
    private val reviewRepository : ReviewRepository,
    private val auth: FirebaseAuth,
): ViewModel() {

    val pokemonInfoData = MutableLiveData<PokemonInfoData>()
    val pokemonReviewData = MutableLiveData<List<PokemonReviewsData>>()
    val averageRating = MutableLiveData<Float>(0.0f)
    val reviewSize = MutableLiveData<Int>(0)
    val firstRateSkillSet = MutableLiveData<Int>(0)
    val secondRateSkillSet = MutableLiveData<Int>(0)
    val isCalculationComplete = MutableLiveData<Boolean>(false)


    fun fetchPokemonInfo(pokemonName: String) {
        viewModelScope.launch {
            pokemonInfoData.value = repository.getInfoData(pokemonName)
        }
    }

    fun addReviewListener(pokemonName: String, query:String){
        viewModelScope.launch {
            val ref = reviewRepository.getQueryInfo(pokemonName,query)
            when(ref){
                is Response.Success -> {
                    val valueEventListener: ValueEventListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val reviewList = ArrayList<PokemonReviewsData>()
                            for (ds in snapshot.children) {
                                val data = ds.getValue(PokemonReviewsData::class.java)!!
                                if(auth.uid != null){
                                    val reported:Boolean = (ds.child(Constants.QUERY_REPORTERS).child(auth.uid!!).value?:false) as Boolean
                                    if(!reported){
                                        reviewList.add(data)
                                    }
                                } else {
                                    reviewList.add(data)
                                }
                                pokemonReviewData.value = reviewList.reversed()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    }
                    ref.data.addListenerForSingleValueEvent(valueEventListener)
                }
                is Response.Failure ->{
                    Log.d("InfoViewModel Query Error", "Query Error")
                }
            }
        }
    }

    fun fetchRating(pokemonName: String){
        viewModelScope.launch {
            val ref = reviewRepository.getReviewRef(pokemonName)
            when(ref){
                is Response.Success -> {
                    attachRatingListener(ref.data)
                }
                is Response.Failure ->{
                    Log.d("InfoViewModel Query Error", "Query Error")
                }
            }
        }
    }

    private fun attachRatingListener(ref: DatabaseReference) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children
                    .mapNotNull { it.getValue(PokemonReviewsData::class.java) }

                reviewSize.value = reviews.size

                if (reviews.isNotEmpty()) {
                    calculateReviewStats(reviews)
                } else {
                    isCalculationComplete.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calculateReviewStats(reviews: List<PokemonReviewsData>) {
        val ratings = reviews.map { it.rating }
        val firstSkill = reviews.map { it.firstSkillSetRate }
        val secondSkill = reviews.map { it.secondSkillSetRate }

        averageRating.value = ratings.average().round(1)
        firstRateSkillSet.value = firstSkill.average().percent()
        secondRateSkillSet.value = secondSkill.average().percent()

        isCalculationComplete.value = true
    }

}

private fun Double.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier.toFloat()
}

private fun Double.percent(): Int {
    return ((1 - this) * 100).toInt()
}