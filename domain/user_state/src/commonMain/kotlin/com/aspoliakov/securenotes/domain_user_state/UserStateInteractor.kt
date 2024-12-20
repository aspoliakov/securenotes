package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_db.DatabaseManager
import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.AuthResult
import com.aspoliakov.securenotes.domain_user_state.model.UserState
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */

class UserStateInteractor(
        private val keyValueStorage: KeyValueStorage,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val auth: FirebaseAuth,
        private val databaseManager: DatabaseManager,
) {

    companion object {
        const val USER_AUTH_STATE = "user_auth_state"
        const val USER_EMAIL = "user_email"
    }

    suspend fun signIn(
            email: String,
            password: String,
    ): AuthResult {
        if (checkUserAlreadyAuthorized()) return AuthResult.SIGN_OUT
        return runCatching {
            auth.signInWithEmailAndPassword(
                    email = email,
                    password = password,
            )
            setUserAuthorized(email)
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    suspend fun signUp(
            email: String,
            password: String,
    ): AuthResult {
        if (checkUserAlreadyAuthorized()) return AuthResult.SIGN_OUT
        return runCatching {
            auth.createUserWithEmailAndPassword(
                    email = email,
                    password = password,
            )
            setUserAuthorized(email)
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    suspend fun logout() {
        auth.signOut()
        databaseManager.clearAll()
        encryptedKeyValueStorage.clear()
        keyValueStorage.clear()
    }

    suspend fun setUserActive() {
        keyValueStorage.put(USER_AUTH_STATE, UserState.ACTIVE.state)
    }

    private suspend fun checkUserAlreadyAuthorized(): Boolean {
        return if (auth.currentUser != null) {
            Napier.e("Current user is not null. Illegal signing in")
            logout()
            true
        } else {
            false
        }
    }

    private fun onAuthFailure(throwable: Throwable): AuthResult {
        Napier.e("Auth error: $throwable")
        return when (throwable) {
            is FirebaseAuthWeakPasswordException -> AuthResult.SIGN_UP_WEAK_PASSWORD
            is FirebaseAuthInvalidCredentialsException -> AuthResult.SIGN_IN_WRONG_CREDENTIALS
            is FirebaseAuthUserCollisionException -> AuthResult.SIGN_UP_USER_ALREADY_REGISTERERD
            is FirebaseNetworkException -> AuthResult.NETWORK_ERROR
            else -> AuthResult.UNEXPECTED_ERROR
        }
    }

    private suspend fun setUserAuthorized(email: String) {
        keyValueStorage.put(USER_EMAIL, email)
        keyValueStorage.put(USER_AUTH_STATE, UserState.AUTHORIZED.state)
    }
}
