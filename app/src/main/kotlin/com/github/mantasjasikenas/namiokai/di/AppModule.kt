package com.github.mantasjasikenas.namiokai.di

import com.github.mantasjasikenas.core.common.di.annotations.ApplicationScope
import com.github.mantasjasikenas.core.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
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
        periodRepository: PeriodRepository,
        @ApplicationScope coroutineScope: CoroutineScope
    ): DebtsManager =
        DebtsManager(
            billsRepository = billsRepository,
            periodRepository = periodRepository,
            coroutineScope = coroutineScope
        )


}