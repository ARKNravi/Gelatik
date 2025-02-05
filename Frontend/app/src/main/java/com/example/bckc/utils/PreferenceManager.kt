package com.example.bckc.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    // Registration data methods
    fun saveRegistrationData(
        email: String,
        fullName: String,
        birthDate: String,
        identityType: String
    ) {
        prefs.edit()
            .putString(KEY_REG_EMAIL, email)
            .putString(KEY_REG_FULLNAME, fullName)
            .putString(KEY_REG_BIRTHDATE, birthDate)
            .putString(KEY_REG_IDENTITY, identityType)
            .apply()
    }

    fun getRegistrationData(): Map<String, String?> {
        return mapOf(
            "email" to prefs.getString(KEY_REG_EMAIL, ""),
            "fullName" to prefs.getString(KEY_REG_FULLNAME, ""),
            "birthDate" to prefs.getString(KEY_REG_BIRTHDATE, ""),
            "identityType" to prefs.getString(KEY_REG_IDENTITY, "")
        )
    }

    fun clearRegistrationData() {
        prefs.edit()
            .remove(KEY_REG_EMAIL)
            .remove(KEY_REG_FULLNAME)
            .remove(KEY_REG_BIRTHDATE)
            .remove(KEY_REG_IDENTITY)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "StuDeafPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_REG_EMAIL = "reg_email"
        private const val KEY_REG_FULLNAME = "reg_fullname"
        private const val KEY_REG_BIRTHDATE = "reg_birthdate"
        private const val KEY_REG_IDENTITY = "reg_identity"
    }
}
