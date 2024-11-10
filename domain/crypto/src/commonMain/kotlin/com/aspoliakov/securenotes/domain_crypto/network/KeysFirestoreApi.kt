package com.aspoliakov.securenotes.domain_crypto.network

import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore

/**
 * Project SecureNotes
 */
class KeysFirestoreApi(
        private val userStateProvider: UserStateProvider,
        private val firestore: FirebaseFirestore,
) : KeysApi {

    companion object {
        private const val USERS_COLLECTION_PATH = "users"
        private const val KEYS_COLLECTION_PATH = "keys"
    }

    override suspend fun postNewUserKey(request: PostKeyPairRequest) {
        getKeysCollection()
                .document(request.id)
                .set(
                        mapOf(
                                "id" to request.id,
                                "public_key" to request.publicKey,
                                "encrypted_private_key" to request.encryptedPrivateKey,
                                "version" to request.version,
                        )
                )
    }

    override suspend fun getUserKeys(): GetKeysResponse {
        val keys = getKeysCollection()
                .get()
                .documents
                .map {
                    GetKeysResponse.KeyPair(
                            id = it.get<String>("id"),
                            publicKey = it.get<String>("public_key"),
                            encryptedPrivateKey = it.get<String>("encrypted_private_key"),
                            version = it.get<Int>("version"),
                    )
                }
        return GetKeysResponse(keys)
    }

    override suspend fun deleteUserKey(keyId: String) {
        getKeysCollection()
                .document(keyId)
                .delete()
    }

    private fun getKeysCollection(): CollectionReference {
        val uid = userStateProvider.getUid() ?: throw IllegalStateException("Uid is null")
        return firestore
                .collection(USERS_COLLECTION_PATH)
                .document(uid)
                .collection(KEYS_COLLECTION_PATH)
    }
}
