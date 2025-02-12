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

    // Profile data methods
    fun saveProfileData(
        fullName: String,
        birthDate: String,
        identityType: String,
        institution: String,
        profilePictureUrl: String?
    ) {
        prefs.edit()
            .putString(KEY_PROFILE_FULLNAME, fullName)
            .putString(KEY_PROFILE_BIRTHDATE, birthDate)
            .putString(KEY_PROFILE_IDENTITY_TYPE, identityType)
            .putString(KEY_PROFILE_INSTITUTION, institution)
            .putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl)
            .apply()
    }

    fun getProfileData(): Map<String, String?> {
        return mapOf(
            "fullName" to prefs.getString(KEY_PROFILE_FULLNAME, ""),
            "birthDate" to prefs.getString(KEY_PROFILE_BIRTHDATE, ""),
            "identityType" to prefs.getString(KEY_PROFILE_IDENTITY_TYPE, ""),
            "institution" to prefs.getString(KEY_PROFILE_INSTITUTION, ""),
            "profilePictureUrl" to prefs.getString(KEY_PROFILE_PICTURE_URL, null)
        )
    }

    fun clearProfileData() {
        prefs.edit()
            .remove(KEY_PROFILE_FULLNAME)
            .remove(KEY_PROFILE_BIRTHDATE)
            .remove(KEY_PROFILE_IDENTITY_TYPE)
            .remove(KEY_PROFILE_INSTITUTION)
            .remove(KEY_PROFILE_PICTURE_URL)
            .apply()
    }

    fun saveVerificationToken(token: String) {
        prefs.edit().putString(KEY_VERIFICATION_TOKEN, token).apply()
    }

    fun getVerificationToken(): String? {
        return prefs.getString(KEY_VERIFICATION_TOKEN, null)
    }

    fun clearVerificationToken() {
        prefs.edit().remove(KEY_VERIFICATION_TOKEN).apply()
    }

    companion object {
        private const val PREF_NAME = "StuDeafPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_REG_EMAIL = "reg_email"
        private const val KEY_REG_FULLNAME = "reg_fullname"
        private const val KEY_REG_BIRTHDATE = "reg_birthdate"
        private const val KEY_REG_IDENTITY = "reg_identity"
        private const val KEY_PROFILE_FULLNAME = "profile_fullname"
        private const val KEY_PROFILE_BIRTHDATE = "profile_birthdate"
        private const val KEY_PROFILE_IDENTITY_TYPE = "profile_identity_type"
        private const val KEY_PROFILE_INSTITUTION = "profile_institution"
        private const val KEY_PROFILE_PICTURE_URL = "profile_picture_url"
        private const val KEY_VERIFICATION_TOKEN = "verification_token"
    }
}
