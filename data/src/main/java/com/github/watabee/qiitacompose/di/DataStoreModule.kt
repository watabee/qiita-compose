package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.datastore.AppDataStore
import com.github.watabee.qiitacompose.datastore.AppDataStoreImpl
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.datastore.UserDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataStoreModule {

    @Binds
    abstract fun bindAppDataStore(instance: AppDataStoreImpl): AppDataStore

    @Binds
    abstract fun bindUserDataStore(instance: UserDataStoreImpl): UserDataStore
}
