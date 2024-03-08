package com.unitewikiapp.unitewiki.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.unitewikiapp.unitewiki.datas.*
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonReviewsViewModel @Inject constructor(
    private val repository:ReviewRepository,
    private val localeStore: LocaleStore
    ) : ViewModel(){

    private val _reviewSnapshot = MutableLiveData<DataSnapshot?>()
    val reviewSnapshot get() = _reviewSnapshot

    private val _reviews = MutableLiveData<List<PokemonReviewsData>>()
    val reviews get() = _reviews

    private val _draft = MutableLiveData<PokemonReviewsData>()
    val draft get() = _draft

    init {
        fetchReviewSnapshot()
    }

    fun fetchReviewSnapshot(){
        viewModelScope.launch {
            val snapShot = repository.fetchReviewSnapshot()
            when (snapShot){
                is Response.Success -> {
                    reviewSnapshot.value = snapShot.data
                }
                else -> {
                    throw Exception("Failed to fetch review data")
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

    fun setDraft(draft:PokemonReviewsData){
        _draft.value = draft
    }

    fun cleanDraft(){
        _draft.value = PokemonReviewsData()
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

    fun getLikeSortedReview(pokemonName: LocaleField):List<PokemonReviewsData>{
        return reviews.value!!.filter {
            it.pokemon!!.localized(localeStore.locale!!) == pokemonName.localized(localeStore.locale!!)
        }.sortedByDescending { it.likes.size }
    }

    fun getTimeSortedReview(pokemonName: LocaleField):List<PokemonReviewsData>{
        return reviews.value!!.filter {
            it.pokemon!!.localized(localeStore.locale!!) == pokemonName.localized(localeStore.locale!!)
        }.sortedByDescending { it.time }
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

    fun filterReportedReview(_reviews:List<PokemonReviewsData>, currentUser: FirebaseUser): List<PokemonReviewsData> {
        return _reviews.filter {
            !(it.reported.containsKey(currentUser.uid))
        }
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
                    ) {
                        fetchReviewSnapshot()
                    }
                })
            } else {
                throw Exception("Failed to update Like")
            }
        }
    }

    fun addReview(
        review: PokemonReviewsData
    ){
        viewModelScope.launch {
            val ref = repository.fetchReviewReference(review)
            if (ref is Response.Success){
                ref.data.setValue(review)
                fetchReviewSnapshot()
            } else {
                throw Exception("Failed to add Review")
            }
        }
    }

    fun removeReview(
        review: PokemonReviewsData
    ){
        viewModelScope.launch {
            val ref = repository.fetchReviewReference(review)
            if (ref is Response.Success){
                ref.data.removeValue()
                fetchReviewSnapshot()
            } else {
                throw Exception("Failed to remove Review")
            }
        }
    }

    fun reportReview(
        review: PokemonReviewsData,
        currentUser: FirebaseUser
    ){
        viewModelScope.launch {
            val ref = repository.fetchReviewReference(review)
            if (ref is Response.Success){
                ref.data.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val p = mutableData.getValue(PokemonReviewsData::class.java)
                            ?: return Transaction.success(mutableData)

                        p.reported[currentUser.uid] = true

                        mutableData.value = p
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        fetchReviewSnapshot()
                    }
                })
            } else {
                throw Exception("Failed to report Review")
            }
        }
    }

}