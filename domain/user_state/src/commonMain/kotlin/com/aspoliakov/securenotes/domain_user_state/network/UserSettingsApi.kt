package com.aspoliakov.securenotes.domain_user_state.network

import com.aspoliakov.securenotes.domain_user_state.model.GetUserSettingsResponse
import com.aspoliakov.securenotes.domain_user_state.model.PostUserSettingsRequest
import com.aspoliakov.securenotes.domain_user_state.model.PostUserSettingsResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface UserSettingsApi {

    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllSettings(
            @Header("access_token") token: String,
    ): GetUserSettingsResponse

    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveSettings(
            @Header("access_token") token: String,
            @Body request: PostUserSettingsRequest,
    ): PostUserSettingsResponse
}
