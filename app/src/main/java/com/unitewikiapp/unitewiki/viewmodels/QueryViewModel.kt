package com.unitewikiapp.unitewiki.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueryViewModel @Inject constructor(): ViewModel() {
    val _query = MutableLiveData<String>()
    val query get() = _query

    fun setQuery(queryData: String) {
        _query.value = queryData
    }
}