package com.aspoliakov.securenotes.domain_crypto.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */
class KeysApiProvider {

    companion object {
        private const val KEYS_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/keys/"
    }

    private val ktorfit by lazy {
        ktorfit {
            Napier.e("Keys api endpoint: $KEYS_API_ENDPOINT")
            baseUrl(KEYS_API_ENDPOINT)
            httpClient(createHttpClient())
        }
    }

    fun provideApi(): KeysApi {
        return ktorfit.createKeysApi()
    }
}
