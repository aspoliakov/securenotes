package com.aspoliakov.securenotes.domain_sync.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit

/**
 * Project SecureNotes
 */
class SyncApiProvider(
        private val authTokenProvider: AuthTokenProvider,
) {

    companion object {
        private const val SYNC_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/"
    }

    private val ktorfit by lazy {
        ktorfit {
            baseUrl(SYNC_API_ENDPOINT)
            httpClient(createHttpClient(authTokenProvider))
        }
    }

    fun provideApi(): SyncApi {
        return ktorfit.createSyncApi()
    }
}
