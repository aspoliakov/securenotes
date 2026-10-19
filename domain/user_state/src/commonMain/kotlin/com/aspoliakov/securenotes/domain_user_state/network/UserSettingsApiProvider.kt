package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */
class UserSettingsApiProvider(
        private val authTokenProvider: AuthTokenProvider,
) {

    companion object {
        private const val USER_SETTINGS_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/settings/"
    }

    private val ktorfit by lazy {
        ktorfit {
            Napier.e("User settings api endpoint: $USER_SETTINGS_API_ENDPOINT")
            baseUrl(USER_SETTINGS_API_ENDPOINT)
            httpClient(createHttpClient(authTokenProvider))
        }
    }

    fun provideApi(): UserSettingsApi {
        return ktorfit.createUserSettingsApi()
    }
}
