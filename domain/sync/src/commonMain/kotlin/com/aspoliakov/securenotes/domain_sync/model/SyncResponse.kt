package com.aspoliakov.securenotes.domain_sync.model

import com.aspoliakov.securenotes.domain_folders.model.FolderDTO
import com.aspoliakov.securenotes.domain_notes.model.NoteDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class SyncResponse(
        @SerialName("sync_revision") val syncRevision: Long,
        @SerialName("notes") val notes: List<NoteDTO>,
        @SerialName("folders") val folders: List<FolderDTO>,
)
