package com.github.watabee.qiitacompose.di

import android.content.Context
import coil.ImageLoader
import coil.util.CoilUtils
import coil.util.Logger
import dagger.BindsOptionalOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.Optional
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ImageModule {

    @BindsOptionalOf
    abstract fun bindOptionalLogger(): Logger

    companion object {

        @Provides
        @Singleton
        fun provideImageLoader(
            @ApplicationContext appContext: Context,
            @Base okHttpClient: OkHttpClient,
            logger: Optional<Logger>
        ): ImageLoader {
            return ImageLoader.Builder(appContext)
                .availableMemoryPercentage(0.15)
                .crossfade(true)
                .okHttpClient {
                    // Don't limit concurrent network requests by host.
                    val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                    okHttpClient.newBuilder()
                        .cache(CoilUtils.createDefaultCache(appContext))
                        .dispatcher(dispatcher)
                        .build()
                }
                .apply {
                    logger.ifPresent(this::logger)
                }
                .build()
        }
    }
}