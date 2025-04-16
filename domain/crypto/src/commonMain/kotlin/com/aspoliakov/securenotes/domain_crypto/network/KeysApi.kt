package com.aspoliakov.securenotes.domain_crypto.network

import com.aspoliakov.securenotes.domain_crypto.model.GetKeysResponse
import com.aspoliakov.securenotes.domain_crypto.model.PostKeyPairRequest
import com.aspoliakov.securenotes.domain_crypto.model.PostKeyPairResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface KeysApi {

    @Headers("Content-Type: application/json")
    @POST("new")
    suspend fun postNewUserKey(
            @Header("access_token") token: String,
            @Body request: PostKeyPairRequest,
    ): PostKeyPairResponse

    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllUserKeys(
            @Header("access_token") token: String,
    ): GetKeysResponse
}
