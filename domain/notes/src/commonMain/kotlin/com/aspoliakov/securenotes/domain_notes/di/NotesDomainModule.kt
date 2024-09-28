package com.aspoliakov.securenotes.domain_notes.di

import com.aspoliakov.securenotes.domain_notes.FolderCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_notes.data.network.NotesApi
import com.aspoliakov.securenotes.domain_notes.data.network.NotesResponse
import io.github.aakira.napier.Napier
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesDomainModule = module {
    single { FolderCreateInteractor(get(), get()) }
    single { NotesCreateInteractor(get()) }
    single { NotesListInteractor(get(), get()) }
}

private fun createFakeNotesApi(): NotesApi {
    return object : NotesApi {

        private val REMOTE_NOTES: List<NotesResponse>

        init {
            REMOTE_NOTES = mutableListOf()
            for (index in 1..5) {
                REMOTE_NOTES.add(
                        NotesResponse(
                                id = "id_$index",
                                name = "name_$index",
                                createdAt = index.toLong(),
                                editedAt = null
                        )
                )
            }
        }

        override suspend fun getNotes(from: Long, size: Int): List<NotesResponse> {
            Napier.e("Get notes from api. From: $from | Size: $size")
            return if (from == 0L) {
                if (size > REMOTE_NOTES.size) REMOTE_NOTES else REMOTE_NOTES.subList(0, size)
            } else {
                val fromIndex = REMOTE_NOTES.indexOfFirst { it.createdAt > from }
                Napier.e("From index: $fromIndex")
                val to = fromIndex + size
                when {
                    fromIndex == -1 -> emptyList()
                    to < REMOTE_NOTES.size -> REMOTE_NOTES.subList(fromIndex, to)
                    else -> REMOTE_NOTES.subList(fromIndex, REMOTE_NOTES.size)
                }
            }
        }
    }
}
