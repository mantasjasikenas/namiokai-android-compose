package com.github.mantasjasikenas.namiokai.data

import android.net.Uri
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): Flow<User>
    suspend fun insertUser(user: User)
    suspend fun clearUsers()
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri>
    suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean>
    suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean>
    suspend fun loadUsersFromStorage(fileName: String): Response<Boolean>
}