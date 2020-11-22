package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.util.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object BaseModule {

    @Provides
    fun provideCoroutineDispatchers(): CoroutineDispatchers =
        CoroutineDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, computation = Dispatchers.Default)
}
