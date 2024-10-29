package com.aspoliakov.securenotes.domain_notes.network

import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore

/**
 * Project SecureNotes
 */

class NotesFirestoreApi(
        private val userStateProvider: UserStateProvider,
        private val firestore: FirebaseFirestore,
) : NotesApi {

    companion object {
        private const val USERS_COLLECTION_PATH = "users"
        private const val NOTES_COLLECTION_PATH = "notes"
    }

    override suspend fun getAllNotes(): List<GetNotesResponse> {
        return getNotesCollection()
                .get()
                .documents
                .map {
                    GetNotesResponse(
                            id = it.get<String>("id"),
                            title = it.get<String?>("title"),
                            body = it.get<String?>("body"),
                    )
                }
    }

    override suspend fun postNote(request: PostNoteRequest) {
        getNotesCollection()
                .document(request.noteId)
                .set(
                        mapOf(
                                "id" to request.noteId,
                                "title" to request.title,
                                "body" to request.body,
                        )
                )
    }

    override suspend fun deleteNote(noteId: String) {
        getNotesCollection()
                .document(noteId)
                .delete()
    }

    private fun getNotesCollection(): CollectionReference {
        val uid = userStateProvider.getUid() ?: throw IllegalStateException("Uid is null")
        return firestore
                .collection(USERS_COLLECTION_PATH)
                .document(uid)
                .collection(NOTES_COLLECTION_PATH)
    }
}
