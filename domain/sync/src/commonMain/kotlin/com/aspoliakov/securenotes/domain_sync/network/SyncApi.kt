package com.aspoliakov.securenotes.domain_sync.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_sync.model.SyncResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface SyncApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @GET("sync")
    suspend fun sync(
            @Query("since") since: Long?,
    ): SyncResponse
}
