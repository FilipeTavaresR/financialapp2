package com.filipetavares.financialapp2.util

class Validators {
    companion object Validators{
        fun emailValidator(email: String): Boolean {
            val regexEmail = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
            return regexEmail.matches(email)
        }
    }
}