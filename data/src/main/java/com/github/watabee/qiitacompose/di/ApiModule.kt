package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.api.QiitaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ApiModule {
    companion object {
        @Provides @Singleton fun provideQiitaApi(retrofit: Retrofit): QiitaApi = retrofit.create()
    }
}
