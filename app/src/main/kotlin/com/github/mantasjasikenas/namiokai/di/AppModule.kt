package com.github.mantasjasikenas.namiokai.di

import com.github.mantasjasikenas.core.data.repository.debts.DebtsService
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
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
        periodRepository: PeriodRepository,
        debtsRepository: DebtsRepository,
    ): DebtsService =
        DebtsService(
            billsRepository = billsRepository,
            periodRepository = periodRepository,
            debtsRepository = debtsRepository
        )
}