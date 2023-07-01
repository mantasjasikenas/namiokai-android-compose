package com.github.mantasjasikenas.namiokai.di

import com.github.mantasjasikenas.namiokai.data.BillsRepository
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.namiokai.di.annotations.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDebtsManager(
        billsRepository: BillsRepository,
        @ApplicationScope coroutineScope: CoroutineScope
    ): DebtsManager = DebtsManager(billsRepository, coroutineScope)


}