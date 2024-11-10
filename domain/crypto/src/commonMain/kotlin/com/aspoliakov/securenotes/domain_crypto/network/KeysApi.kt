package com.aspoliakov.securenotes.domain_crypto.network

/**
 * Project SecureNotes
 */
interface KeysApi {

    suspend fun postNewUserKey(request: PostKeyPairRequest)

    suspend fun getUserKeys(): GetKeysResponse

    suspend fun deleteUserKey(keyId: String)
}
