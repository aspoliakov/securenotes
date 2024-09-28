package com.aspoliakov.securenotes.core_base.util

/**
 * Project SecureNotes
 */
object Patterns {

    fun checkEmailIsValid(email: String): Boolean {
        val emailRegex = Regex(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        )
        return email.matches(emailRegex)
    }
}
