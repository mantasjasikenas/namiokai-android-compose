package com.github.mantasjasikenas.namiokai.data

interface BaseFirebaseRepository {
    suspend fun backupCollection(
        collectionPath: String,
        backupPath: String,
        fileName: String
    )

    suspend fun getFileFromStorage(fileName: String): String
    suspend fun uploadJsonToStorage(
        json: String,
        fileName: String
    )
}