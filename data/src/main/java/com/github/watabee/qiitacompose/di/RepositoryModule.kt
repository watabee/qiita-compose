package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.repository.QiitaRepositoryImpl
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds abstract fun bindQiitaRepository(instance: QiitaRepositoryImpl): QiitaRepository

    @Binds abstract fun bindUserRepository(instance: UserRepositoryImpl): UserRepository
}
