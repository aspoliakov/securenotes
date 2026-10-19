package com.aspoliakov.securenotes.core_network.auth

/**
 * Project SecureNotes
 */

interface AuthTokenProvider {

    suspend fun getAccessToken(): String?

    suspend fun refreshAccessToken(rejectedAccessToken: String): String?
}
