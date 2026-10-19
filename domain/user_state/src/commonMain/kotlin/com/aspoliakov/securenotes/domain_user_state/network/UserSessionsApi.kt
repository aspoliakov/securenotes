package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_user_state.model.TerminateOtherSessionsResponse
import com.aspoliakov.securenotes.domain_user_state.model.TerminateSessionResponse
import com.aspoliakov.securenotes.domain_user_state.model.UserSessionDTO
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface UserSessionsApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @GET("sessions")
    suspend fun getSessions(): List<UserSessionDTO>

    @Authorized
    @Headers("Content-Type: application/json")
    @DELETE("sessions/{session_id}")
    suspend fun terminateSession(
            @Path("session_id") sessionId: String,
    ): TerminateSessionResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @DELETE("sessions")
    suspend fun terminateOtherSessions(): TerminateOtherSessionsResponse
}
