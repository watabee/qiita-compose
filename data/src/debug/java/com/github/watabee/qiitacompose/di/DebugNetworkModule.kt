package com.github.watabee.qiitacompose.di

import android.util.Log
import coil.util.DebugLogger
import coil.util.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DebugNetworkModule {

    @Provides
    fun provideLogger(): Logger = DebugLogger(Log.VERBOSE)
}
