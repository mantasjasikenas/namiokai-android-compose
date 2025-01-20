package com.github.mantasjasikenas.namiokai.di

import com.github.mantasjasikenas.core.data.repository.debts.DebtsService
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDebtsManager(
        billsRepository: BillsRepository,
        debtsRepository: DebtsRepository,
    ): DebtsService =
        DebtsService(
            billsRepository = billsRepository,
            debtsRepository = debtsRepository
        )
}