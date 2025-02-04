package com.example.bckc.data.api.interceptor

import com.example.bckc.utils.PreferenceManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            runBlocking {
                preferenceManager.clearToken()
            }
        }
        return null
    }
} 