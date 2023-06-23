package com.unitewikiapp.unitewiki.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface RealTimeDataBase {

    companion object {
        fun initiate(): FirebaseDatabase {
            return Firebase.database
        }
    }

}