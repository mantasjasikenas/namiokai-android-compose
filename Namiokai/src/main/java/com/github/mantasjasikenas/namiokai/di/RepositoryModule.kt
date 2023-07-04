package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.namiokai.data.BaseFirebaseRepository
import com.github.mantasjasikenas.namiokai.data.BillsRepository
import com.github.mantasjasikenas.namiokai.data.FlatBillsRepository
import com.github.mantasjasikenas.namiokai.data.PurchaseBillsRepository
import com.github.mantasjasikenas.namiokai.data.TripBillsRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.data.repository.BaseFirebaseRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.BillsRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.FlatBillsRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.PurchaseBillsRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.TripBillsRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.UsersRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBaseFirebaseRepository(
        db: FirebaseFirestore,
        storage: FirebaseStorage
    ): BaseFirebaseRepository =
        BaseFirebaseRepositoryImpl(
            db,
            storage
        )

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideBillsRepository(
        purchaseBillsRepository: PurchaseBillsRepository,
        tripBillsRepository: TripBillsRepository,
        flatBillsRepository: FlatBillsRepository,
    ): BillsRepository =
        BillsRepositoryImpl(
            purchaseBillsRepository,
            tripBillsRepository,
            flatBillsRepository
        )

    @Provides
    @Singleton
    fun providePurchaseBillsRepository(
        baseFirebaseRepository: BaseFirebaseRepository,
        db: FirebaseFirestore
    ): PurchaseBillsRepository =
        PurchaseBillsRepositoryImpl(
            baseFirebaseRepository = baseFirebaseRepository,
            db = db
        )

    @Provides
    @Singleton
    fun provideTripBillsRepository(
        baseFirebaseRepository: BaseFirebaseRepository,
        db: FirebaseFirestore,
        storage: FirebaseStorage
    ): TripBillsRepository =
        TripBillsRepositoryImpl(
            baseFirebaseRepository = baseFirebaseRepository,
            db = db,
            storage = storage
        )

    @Provides
    @Singleton
    fun provideFlatBillsRepository(
        db: FirebaseFirestore,
        baseFirebaseRepository: BaseFirebaseRepository
    ): FlatBillsRepository =
        FlatBillsRepositoryImpl(
            db = db,
            baseFirebaseRepository = baseFirebaseRepository
        )

    @Provides
    @Singleton
    fun provideUsersRepository(
        baseFirebaseRepository: BaseFirebaseRepository,
        db: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): UsersRepository =
        UsersRepositoryImpl(
            baseFirebaseRepository = baseFirebaseRepository,
            db = db,
            storage = storage,
            auth = auth
        )


}