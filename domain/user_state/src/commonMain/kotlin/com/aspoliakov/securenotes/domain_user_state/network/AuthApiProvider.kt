package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */
class AuthApiProvider {

    companion object {
        private const val AUTH_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/users/"
    }

    private val ktorfit by lazy {
        ktorfit {
            Napier.e("Auth api endpoint: $AUTH_API_ENDPOINT")
            baseUrl(AUTH_API_ENDPOINT)
            httpClient(createHttpClient())
        }
    }

    fun provideApi(): AuthApi {
        return ktorfit.createAuthApi()
    }
}
