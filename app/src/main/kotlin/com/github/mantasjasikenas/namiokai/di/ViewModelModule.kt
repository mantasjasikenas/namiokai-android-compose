package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.core.common.util.ToastManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideToastManager(@ApplicationContext context: Context): ToastManager {
        return ToastManager(context)
    }

}