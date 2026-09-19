package com.aspoliakov.securenotes.domain_notes.model

/**
 * Project SecureNotes
 */

enum class NoteColor(val argb: Long?) {

    DEFAULT(null),
    RED(0xFFEF9A9AL),
    PINK(0xFFF48FB1L),
    PURPLE(0xFFCE93D8L),
    DEEP_PURPLE(0xFFB39DDBL),
    INDIGO(0xFF9FA8DAL),
    BLUE(0xFF90CAF9L),
    TEAL(0xFF80CBC4L),
    GREEN(0xFFA5D6A7L),
    AMBER(0xFFFFE082L),
    BROWN(0xFFBCAAA4L);

    companion object {

        fun fromArgb(argb: Long?): NoteColor {
            return entries.firstOrNull { it.argb == argb } ?: DEFAULT
        }
    }
}
