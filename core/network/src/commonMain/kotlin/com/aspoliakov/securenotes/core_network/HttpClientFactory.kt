package com.aspoliakov.securenotes.core_network

import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponse
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
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

/**
 * TCP connect only — no payload involved, so it can be short: an unreachable backend
 * host fails fast, while a live one on a slow mobile network still connects in time.
 */
private const val CONNECT_TIMEOUT_MS = 5_000L

/**
 * Max silence between two packets of an established connection.
 */
private const val SOCKET_TIMEOUT_MS = 15_000L

/**
 * Upper bound for the whole call, including large note sync payloads.
 */
private const val REQUEST_TIMEOUT_MS = 30_000L

fun createHttpClient(): HttpClient {
    return HttpClient {
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
}
