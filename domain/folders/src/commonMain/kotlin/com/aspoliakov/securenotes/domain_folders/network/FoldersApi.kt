package com.aspoliakov.securenotes.domain_folders.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_folders.model.DeleteFolderResponse
import com.aspoliakov.securenotes.domain_folders.model.PostFolderRequest
import com.aspoliakov.securenotes.domain_folders.model.PostFolderResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface FoldersApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveFolder(
            @Body request: PostFolderRequest,
    ): PostFolderResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @DELETE("{folder_id}")
    suspend fun deleteFolder(
            @Path("folder_id") folderId: String,
    ): DeleteFolderResponse
}
