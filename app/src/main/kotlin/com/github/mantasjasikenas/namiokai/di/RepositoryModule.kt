package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.core.data.repository.BaseFirebaseRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.BillsRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.FlatBillsRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.InvitationRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.PeriodRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.PurchaseBillsRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.SpaceRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.TripBillsRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.UserDataRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.UsersRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.debts.DebtsRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.github.mantasjasikenas.core.domain.repository.InvitationRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
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
    fun providePeriodRepository(
        spaceRepository: SpaceRepository
    ): PeriodRepository {
        return PeriodRepositoryImpl(spaceRepository = spaceRepository)
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
    ): TripBillsRepository =
        TripBillsRepositoryImpl(
            baseFirebaseRepository = baseFirebaseRepository,
            db = db,
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

    @Provides
    @Singleton
    fun provideUserDataRepository(
        preferencesRepository: PreferencesRepository,
        usersRepository: UsersRepository,
        periodRepository: PeriodRepository
    ): UserDataRepository =
        UserDataRepositoryImpl(
            preferencesRepository = preferencesRepository,
            usersRepository = usersRepository,
            periodRepository = periodRepository
        )

    @Provides
    @Singleton
    fun provideDebtsRepository(): DebtsRepository {
        return DebtsRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSpaceRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ): SpaceRepository =
        SpaceRepositoryImpl(
            db = db,
            auth = auth
        )

    @Provides
    @Singleton
    fun provideInvitationRepository(
        db: FirebaseFirestore,
        spaceRepository: SpaceRepository
    ): InvitationRepository =
        InvitationRepositoryImpl(
            db = db,
            spaceRepository = spaceRepository
        )
}