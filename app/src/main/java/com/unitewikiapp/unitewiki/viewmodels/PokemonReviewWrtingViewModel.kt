package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitewikiapp.unitewiki.datas.*
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonReviewWrtingViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val infoRepository: InfoRepository,
):ViewModel() {

    val infoData = MutableLiveData<PokemonInfoData>()
    val myReview = MutableLiveData<String>()
    val firstClickState = MutableLiveData<Boolean?>()
    var secondClickState = MutableLiveData<Boolean?>()

    fun fetchInfo(pokemonName: String) {
        viewModelScope.launch {
            infoData.value = infoRepository.getInfoData(pokemonName)
        }
    }

    fun fetchMyReviewText(pokemonName: String, myuid:String){
        viewModelScope.launch {
            val ref = reviewRepository.getMyReviewRef(pokemonName,myuid)
            when(ref){
                is Response.Success -> {
                    ref.data.get().addOnSuccessListener {
                        val data = it.getValue(PokemonReviewsData::class.java)
                        myReview.value = data?.writing
                    }
                }
                is Response.Failure ->{
                    Log.d("Error during REALTIME DB", "Query Error")
                }
            }
        }
    }

    fun commitPosting(pokemonName: String, myUid:String, data:PokemonReviewWritingData){
        viewModelScope.launch {
            val ref = reviewRepository.getReviewRef(pokemonName)
            when(ref){
                is Response.Success -> {
                    ref.data.child(myUid).setValue(data)
                }
                is Response.Failure ->{
                    Log.d("Error during REALTIME DB", "Query Error")
                }
            }
        }
    }


}