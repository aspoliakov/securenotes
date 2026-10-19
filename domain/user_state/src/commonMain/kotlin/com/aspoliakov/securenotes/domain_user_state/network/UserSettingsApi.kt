package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_user_state.model.GetUserSettingsResponse
import com.aspoliakov.securenotes.domain_user_state.model.PostUserSettingsRequest
import com.aspoliakov.securenotes.domain_user_state.model.PostUserSettingsResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface UserSettingsApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllSettings(): GetUserSettingsResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveSettings(
            @Body request: PostUserSettingsRequest,
    ): PostUserSettingsResponse
}
