package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.unitewikiapp.unitewiki.datas.*
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

    fun sortPokemonByScore(unSorted: ArrayList<PokemonRankData>): ArrayList<PokemonRankData>{
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

    fun getReviewCount(pokemonName: LocaleField): Int{
        return reviews.value!!.count {
            it.pokemon!!.localized(localeStore.locale!!) == pokemonName.localized(localeStore.locale!!)
        }
    }

    fun getAverageScore(pokemonName: LocaleField): Float {
        val averageScoreMap = reviews.value!!.groupBy {
            it.pokemon!!.localized(localeStore.locale!!)
        }.mapValues { (key, reviews) ->
            reviews.map { it.rating }.average().toFloat()
        }
        val averageScore = averageScoreMap[pokemonName.localized(localeStore.locale!!)] ?: 0.0f
        return Math.round(averageScore*10)/10.0f
    }

    fun getSkillPreference(pokemonName: LocaleField):ArrayList<Int>  {
        val counts = ArrayList(listOf(0, 0, 0, 0))
        val _review = reviews.value!!.filter {
            it.pokemon!!.localized(localeStore.locale!!) == pokemonName.localized(localeStore.locale!!)
        }
        _review.forEach { review ->
            val parts = review.selectedSkills.split(",").map { it.toInt() }
            val firstNum = parts[0]
            val secondNum = parts[1]

            counts[firstNum-1] += 1
            counts[secondNum+1] += 1
        }
        return counts
    }

    fun getSortedReview(pokemonName: LocaleField):List<PokemonReviewsData>{
        return reviews.value!!.filter {
            it.pokemon!!.localized(localeStore.locale!!) == pokemonName.localized(localeStore.locale!!)
        }.sortedByDescending { it.likes.size }
    }

    fun calculatePreference(a: Int, b: Int):Int{
        if(a==0 && b==0){
            return 0
        }
        if(a==b){
            return 50
        }
        return Math.max(a/(a+b),b/(a+b))*100
    }

    fun updateLike(
        review: PokemonReviewsData,
        currentUser: FirebaseUser
    ){
        viewModelScope.launch {
            val ref = repository.fetchReviewReference(review)
            if(ref is Response.Success){
                ref.data.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val p = mutableData.getValue(PokemonReviewsData::class.java)
                            ?: return Transaction.success(mutableData)

                        if (p.likes.containsKey(currentUser.uid)) {
                            p.likes.remove(currentUser.uid)
                        } else {
                            p.likes[currentUser.uid] = true
                        }

                        mutableData.value = p
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) { }
                })
            } else {
                throw Exception("Failed to update Like")
            }
        }
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