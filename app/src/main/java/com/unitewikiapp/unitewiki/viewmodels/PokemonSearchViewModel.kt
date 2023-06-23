package com.unitewikiapp.unitewiki.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitewikiapp.unitewiki.datas.InfoRepository
import com.unitewikiapp.unitewiki.datas.PokemonSearchData
import com.unitewikiapp.unitewiki.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonSearchViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
): ViewModel() {

    var pokemondatalist = MutableLiveData<ArrayList<PokemonSearchData>>()
    val query = MutableLiveData<String>()

    fun getSearchData() {
        viewModelScope.launch {
            val ref = infoRepository.getSearchDataRef()
            if(ref is Response.Success){
                ref.data.get().addOnSuccessListener { snapshot->
                    val list = arrayListOf<PokemonSearchData>()
                    snapshot.children.forEach { rankSnapshot ->
                        val data = rankSnapshot.getValue(PokemonSearchData::class.java)!!
                        list.add(data)
                    }
                    pokemondatalist.value = list
                }
            }else{
                Log.d("Error during REALTIME_DB","FIREBASE ERROR")
            }
        }
    }

    fun setQuery(queryData: String) {
        query.value = queryData
    }

    fun getQuery(): LiveData<String>? {
        return query
    }

}