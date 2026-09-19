package com.aspoliakov.securenotes.domain_folders.network

import com.aspoliakov.securenotes.domain_folders.model.DeleteFolderResponse
import com.aspoliakov.securenotes.domain_folders.model.GetFoldersResponse
import com.aspoliakov.securenotes.domain_folders.model.PostFolderRequest
import com.aspoliakov.securenotes.domain_folders.model.PostFolderResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface FoldersApi {

    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveFolder(
            @Header("access_token") token: String,
            @Body request: PostFolderRequest,
    ): PostFolderResponse

    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllFolders(
            @Header("access_token") token: String,
    ): GetFoldersResponse

    @Headers("Content-Type: application/json")
    @DELETE("{folder_id}")
    suspend fun deleteFolder(
            @Header("access_token") token: String,
            @Path("folder_id") folderId: String,
    ): DeleteFolderResponse
}
