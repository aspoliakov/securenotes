package com.aspoliakov.securenotes.domain_notes.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponse
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
            httpClient(
                    HttpClient {
                        install(Logging) {
                            logger = object : Logger {
                                override fun log(message: String) {
                                    Napier.d(message, null, "Ktor HTTP Client")
                                }
                            }
                            level = LogLevel.ALL
                        }
                        install(ContentNegotiation) {
                            json(
                                    Json {
                                        explicitNulls = false
                                        prettyPrint = true
                                        isLenient = true
                                        ignoreUnknownKeys = true
                                    }
                            )
                        }
                        expectSuccess = true
                        HttpResponseValidator {
                            handleResponseExceptionWithRequest { exception, _ ->
                                val clientException = exception as? ClientRequestException
                                        ?: return@handleResponseExceptionWithRequest
                                val response = clientException.response
                                if (response.status == HttpStatusCode.NotFound) {
                                    throw Error404Exception()
                                } else {
                                    val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
                                    throw ErrorResponseException(errorResponse.detail)
                                }
                            }
                        }
                    }
            )
        }
    }

    fun provideApi(): NotesApi {
        return ktorfit.createNotesApi()
    }
}