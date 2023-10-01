package com.github.watabee.qiitacompose.di

import com.github.watabee.qiitacompose.BuildConfig
import com.github.watabee.qiitacompose.util.Env
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppModule {

    companion object {
        @Provides
        fun provideEnv(): Env = Env(qiitaClientId = BuildConfig.QIITA_CLIENT_ID, qiitaClientSecret = BuildConfig.QIITA_CLIENT_SECRET)
    }
}
