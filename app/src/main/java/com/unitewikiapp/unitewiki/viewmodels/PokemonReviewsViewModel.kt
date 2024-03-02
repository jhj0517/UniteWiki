package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.unitewikiapp.unitewiki.datas.PokemonRankData
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.datas.ReviewRepository
import com.unitewikiapp.unitewiki.datas.localized
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonReviewsViewModel @Inject constructor(
    private val repository:ReviewRepository,
    private val auth: FirebaseAuth,
    private val localeStore: LocaleStore
    ) : ViewModel(){

    val pokemonName = MutableLiveData<String>()
    val reviewData = MutableLiveData<List<PokemonReviewsData>>()
    val myReviewData = MutableLiveData<ArrayList<PokemonReviewsData>>()
    lateinit var valueEventListener: ValueEventListener

    private val _reviewSnapshot = MutableLiveData<DataSnapshot?>()
    val reviewSnapshot get() = _reviewSnapshot

    private val _reviews = MutableLiveData<List<PokemonReviewsData>>()
    val reviews get() = _reviews

    init {
        fetchReviewSnapshot()
    }

    private fun fetchReviewSnapshot(){
        viewModelScope.launch {
            val snapShot = repository.fetchReviewSnapshot()
            when (snapShot){
                is Response.Success -> {
                    reviewSnapshot.value = snapShot.data
                }
                else -> {
                    throw Exception("Failed to fetch data")
                }
            }
        }
    }

    fun setReviews(){
        val reviewsList = ArrayList<PokemonReviewsData>()
        reviewSnapshot.value!!.children.forEach{ pokemonSnap->
            pokemonSnap.children.forEach { reviewSnap ->
                val data = reviewSnap.getValue(PokemonReviewsData::class.java)!!
                reviewsList.add(data)
            }
        }
        _reviews.value = reviewsList
    }

    fun sortByScore(unSorted: ArrayList<PokemonRankData>): ArrayList<PokemonRankData>{
        val averageScoreMap = reviews.value!!.groupBy { it.pokemon!!.localized(localeStore.locale!!) }
            .mapValues { (key, reviews) ->
                reviews.map { it.rating }.average().toFloat()
            }

        val updatedRanks = unSorted.map { rank ->
            rank.apply {
                average_rating = averageScoreMap[pokemon_name] ?: 0.0f
            }
        }.sortedByDescending { it.average_rating }
        return ArrayList(updatedRanks)
    }

    fun setReportTrue(pokemonName: String, uid:String, uidOfWriter:String){
        viewModelScope.launch {
            val reportRef = repository.getQueryForReport(pokemonName,uid,uidOfWriter)
            if(reportRef is Response.Success){
                reportRef.data.setValue(true)
            } else{
                Log.d("ReviewReportQueryError", "Query Error")
            }
        }
    }

//    fun onLikeClicked(pokemonName: String, myUid:String, uidOfWriter:String) {
//        viewModelScope.launch {
//            val ref = repository.getQueryForMyReview(pokemonName,uidOfWriter)
//            if(ref is Response.Success){
//            ref.data.runTransaction(object : Transaction.Handler {
//                override fun doTransaction(mutableData: MutableData): Transaction.Result {
//                    val p = mutableData.getValue(PokemonReviewsData::class.java)
//                        ?: return Transaction.success(mutableData)
//
//                    if (p.likes.containsKey(myUid)) {
//                        // Unstar the post and remove self from stars
//                        p.likesNumber = p.likesNumber - 1
//                        p.likes.remove(myUid)
//                    } else {
//                        // Star the post and add self to stars
//                        p.likesNumber = p.likesNumber + 1
//                        p.likes[myUid] = true
//                    }
//
//                    // Set value and report transaction success
//                    mutableData.value = p
//                    return Transaction.success(mutableData)
//                }
//
//                override fun onComplete(
//                    databaseError: DatabaseError?,
//                    committed: Boolean,
//                    currentData: DataSnapshot?
//                ) { }
//            })
//            } else {
//                Log.d("Error during REALTIME_DB", " Query Error" )
//            }
//        }
//    }

    fun removeMyReview(pokemonName: String, uid:String){
        viewModelScope.launch {
            val myReviewRef = repository.getQueryForMyReview(pokemonName,uid)
            if(myReviewRef is Response.Success){
                myReviewRef.data.removeValue()
            } else{
                Log.d("Error during REALTIME_DB", "My Review Query Error")
            }
        }
    }

    fun addReviewValueListener(pokemonName: String, query:String){
        viewModelScope.launch {
            val pokemonReviewRef = repository.getQuery(pokemonName,query)
            when(pokemonReviewRef){
                is Response.Success -> {
                    valueEventListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val reviewList = ArrayList<PokemonReviewsData>()
                            val myReviewList = ArrayList<PokemonReviewsData>()
                            for (ds in snapshot.children) {
                                val data = ds.getValue<PokemonReviewsData>()?: PokemonReviewsData()

                                if(auth.uid != null && data.uid != auth.uid){
                                    val reported:Boolean = (ds.child(Constants.QUERY_REPORTERS).child(auth.uid!!).value?:false) as Boolean
                                    data.isLiked = (ds.child(Constants.QUERY_LIKES).child(auth.uid!!).value?: false) as Boolean
                                    if(!reported){
                                        reviewList.add(data)
                                    }
                                } else if(auth.uid != null && data.uid == auth.uid){
                                    data.isLiked = (ds.child(Constants.QUERY_LIKES).child(auth.uid!!).value?: false) as Boolean
                                    myReviewList.add(data)
                                    myReviewData.value = myReviewList
                                } else if(auth.uid == null){
                                    data.isLiked = false
                                    reviewList.add(data)
                                }
                                reviewData.value = reviewList.reversed()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    }
                    pokemonReviewRef.data.addValueEventListener(valueEventListener)
                }
                is Response.Failure ->{
                    Log.d("Error during REALTIME_DB", "Query Error")
                }
            }
        }
    }

    fun removeReviewValueListener(pokemonName: String, query:String){
        viewModelScope.launch {
            val ref = repository.getQuery(pokemonName, query)
            when(ref){
                is Response.Success -> {
                    ref.data.removeEventListener(valueEventListener)
                }
                is Response.Failure ->{
                    Log.d("Error during REALTIME_DB", "Query Error")
                }
            }
        }
    }


}