package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.core_base.BACKEND_BASE_URL
import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponse
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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

    fun provideApi(): AuthApi {
        return ktorfit.createAuthApi()
    }
}
