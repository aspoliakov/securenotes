package com.aspoliakov.securenotes.domain_notes.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.createHttpClient
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */
class NotesApiProvider {

    companion object {
        private const val NOTES_API_ENDPOINT = BACKEND_BASE_URL + "api/v1/notes/"
    }

    private val ktorfit by lazy {
        ktorfit {
            Napier.e("Notes api endpoint: $NOTES_API_ENDPOINT")
            baseUrl(NOTES_API_ENDPOINT)
            httpClient(createHttpClient())
        }
    }

    fun provideApi(): NotesApi {
        return ktorfit.createNotesApi()
    }
}
