package com.aspoliakov.securenotes.core_network.auth

/**
 * Project SecureNotes
 */

/**
 * Marks a Ktorfit API method as authorized: [AuthTokenPlugin] attaches the access token to it
 * and refreshes the token on 401. Ktorfit forwards function annotations to the request attributes.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Authorized
