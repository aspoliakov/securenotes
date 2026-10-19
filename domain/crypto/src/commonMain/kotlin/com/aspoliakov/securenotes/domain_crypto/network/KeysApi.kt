package com.aspoliakov.securenotes.domain_crypto.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_crypto.model.GetKeysResponse
import com.aspoliakov.securenotes.domain_crypto.model.PostKeyPairRequest
import com.aspoliakov.securenotes.domain_crypto.model.PostKeyPairResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface KeysApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @POST("new")
    suspend fun postNewUserKey(
            @Body request: PostKeyPairRequest,
    ): PostKeyPairResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllUserKeys(): GetKeysResponse
}
