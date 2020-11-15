package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.repository.QiitaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds abstract fun bindQiitaRepository(instance: QiitaRepositoryImpl): QiitaRepository
}
