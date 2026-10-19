package com.aspoliakov.securenotes.domain_folders.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */
class FoldersApiProvider(
        private val authTokenProvider: AuthTokenProvider,
) {

    companion object {
        private const val FOLDERS_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/folders/"
    }

    private val ktorfit by lazy {
        ktorfit {
            Napier.e("Folders api endpoint: $FOLDERS_API_ENDPOINT")
            baseUrl(FOLDERS_API_ENDPOINT)
            httpClient(createHttpClient(authTokenProvider))
        }
    }

    fun provideApi(): FoldersApi {
        return ktorfit.createFoldersApi()
    }
}
