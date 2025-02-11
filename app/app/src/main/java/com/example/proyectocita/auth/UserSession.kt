package com.example.proyectocita.auth

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    }

    fun saveUser(userId: String) {
        prefs.edit().putString("USER_ID", userId).apply()
    }

    fun getUser(): String? {
        return prefs.getString("USER_ID", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
