package com.aspoliakov.securenotes.core_network

import com.aspoliakov.securenotes.core_network.auth.ACCESS_TOKEN_HEADER
import com.aspoliakov.securenotes.core_network.auth.AuthTokenPlugin
import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponse
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
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

private const val CONNECT_TIMEOUT_MS = 5_000L
private const val SOCKET_TIMEOUT_MS = 15_000L
private const val REQUEST_TIMEOUT_MS = 30_000L

fun createHttpClient(authTokenProvider: AuthTokenProvider? = null): HttpClient {
    return HttpClient {
        if (authTokenProvider != null) {
            install(AuthTokenPlugin) {
                tokenProvider = authTokenProvider
            }
        }
        install(HttpTimeout) {
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(message, null, "Ktor HTTP Client")
                }
            }
            level = LogLevel.ALL
            sanitizeHeader { it == ACCESS_TOKEN_HEADER }
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
                    throw ErrorResponseException(
                            statusCode = response.status.value,
                            detail = errorResponse.detail,
                    )
                }
            }
        }
    }
}
