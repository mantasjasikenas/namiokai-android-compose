@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.core.data.repository

import android.net.Uri
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject


private const val USERS_COLLECTION = "users"
private const val BACKUP_USERS_PATH = "backup/users"
private const val IMAGES_STORAGE_PATH = "images"
private const val PHOTO_URL_FIELD = "photoUrl"
private const val USERNAME_FIELD = "displayName"
private const val UID_FIELD = "uid"

const val USERS_IMPORT_FILE_NAME = "users.json"

class UsersRepositoryImpl @Inject constructor(
    private val baseFirebaseRepository: BaseFirebaseRepository,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    db: FirebaseFirestore
) : UsersRepository {
    private val usersCollection = db.collection(USERS_COLLECTION)

    override val currentUser: Flow<User>
        get() {
            return getAuthState()
                .flatMapLatest { isUserLoggedOut ->
                    if (isUserLoggedOut) {
                        callbackFlow {
                            trySend(User())
                            awaitClose()
                        }
                    } else {
                        getUser(auth.currentUser!!.uid).map { it ?: User() }
                    }
                }
        }

    override fun getUsers(): Flow<List<User>> {
        return usersCollection.dataObjects<User>()
    }

    override fun getUsers(userIds: List<String>): Flow<List<User>> {
        return usersCollection
            .whereIn(UID_FIELD, userIds)
            .dataObjects<User>()
    }

    override fun getUser(uid: String): Flow<User?> {
        return usersCollection
            .document(uid)
            .dataObjects<User>()
    }

    override suspend fun insertUser(user: User) {
        usersCollection
            .document(user.uid)
            .set(user)
    }

    override suspend fun clearUsers() {
        usersCollection
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersCollection
                        .document(document.id)
                        .delete()
                }
            }
    }

    override suspend fun loadUsersFromStorage(fileName: String): Response<Boolean> {
        return try {
            val usersJson =
                baseFirebaseRepository.getFileFromStorage("$BACKUP_USERS_PATH/$fileName")
            val users = Json.decodeFromString<List<User>>(usersJson)

            users.forEach { insertUser(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri> {
        return try {
            auth.uid?.let { uid ->
                val profileImageName = "$uid.jpg"

                val downloadUrl =
                    storage.reference.child(IMAGES_STORAGE_PATH)
                        .child(profileImageName)
                        .putFile(imageUri)
                        .await()
                        .storage.downloadUrl.await()

                return Response.Success(downloadUrl)
            }

            Response.Failure(Exception("User is not logged in"))

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean> {
        return try {
            auth.uid?.let { uid ->
                usersCollection
                    .document(uid)
                    .update(
                        PHOTO_URL_FIELD,
                        downloadUrl.toString()
                    )
                    .await()
                return Response.Success(true)
            }
            Response.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean> {
        return try {
            auth.uid?.let { uid ->
                usersCollection
                    .document(uid)
                    .update(
                        USERNAME_FIELD,
                        newUserName
                    )
                    .await()
                return Response.Success(true)
            }
            Response.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun backupCollection(fileName: String) {
        baseFirebaseRepository.backupCollection(
            USERS_COLLECTION,
            BACKUP_USERS_PATH,
            fileName
        )
    }

    private fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}