package com.github.mantasjasikenas.core.domain.repository

import android.net.Uri
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    val currentUser: Flow<User>
    fun getUsers(): Flow<List<User>>
    fun getUser(uid: String): Flow<User>
    suspend fun insertUser(user: User)
    suspend fun clearUsers()
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri>
    suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean>
    suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean>
    suspend fun loadUsersFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)
}