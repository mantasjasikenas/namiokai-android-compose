package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.core.common.localization.LocalizationManager
import com.github.mantasjasikenas.core.data.repository.debts.DebtsService
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideLocalizationManager(
        @ApplicationContext context: Context
    ): LocalizationManager {
        return LocalizationManager(
            context = context
        )
    }
}