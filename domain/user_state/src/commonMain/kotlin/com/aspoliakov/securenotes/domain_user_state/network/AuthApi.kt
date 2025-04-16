package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.domain_user_state.model.AuthenticateRequest
import com.aspoliakov.securenotes.domain_user_state.model.AuthenticateResponse
import com.aspoliakov.securenotes.domain_user_state.model.RegisterRequest
import com.aspoliakov.securenotes.domain_user_state.model.RegisterResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

/**
 * Project SecureNotes
 */

interface AuthApi {

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(
            @Body request: RegisterRequest,
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("authenticate")
    suspend fun authenticate(
            @Body request: AuthenticateRequest,
    ): AuthenticateResponse
}
