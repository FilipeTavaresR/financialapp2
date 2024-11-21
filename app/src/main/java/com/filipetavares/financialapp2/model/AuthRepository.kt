package com.filipetavares.financialapp2.model

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(){

    private val auth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getUserID():String?{
        return auth.currentUser?.uid
    }




}