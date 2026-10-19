package com.aspoliakov.securenotes.core_network.auth

import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode

/**
 * Project SecureNotes
 */

const val ACCESS_TOKEN_HEADER = "access_token"

class AuthTokenPluginConfig {
    lateinit var tokenProvider: AuthTokenProvider
}

val AuthTokenPlugin = createClientPlugin("AuthTokenPlugin", ::AuthTokenPluginConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    on(Send) { request ->
        if (request.annotations.none { it is Authorized }) return@on proceed(request)
        val accessToken = checkNotNull(tokenProvider.getAccessToken()) { "Authorized request without access token" }
        request.headers[ACCESS_TOKEN_HEADER] = accessToken
        val call = proceed(request)
        if (call.response.status != HttpStatusCode.Unauthorized) {
            return@on call
        } else {
            val refreshedAccessToken = tokenProvider.refreshAccessToken(
                    rejectedAccessToken = accessToken,
            ) ?: return@on call
            request.headers[ACCESS_TOKEN_HEADER] = refreshedAccessToken
            proceed(request)
        }
    }
}
