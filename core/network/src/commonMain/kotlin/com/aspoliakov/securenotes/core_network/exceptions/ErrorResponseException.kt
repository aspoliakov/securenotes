package com.aspoliakov.securenotes.core_network.exceptions

/**
 * Project SecureNotes
 */

class ErrorResponseException(
        val statusCode: Int,
        val detail: String?,
) : Exception()
