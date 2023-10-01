package com.github.watabee.qiitacompose.di

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
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

    @BindsOptionalOf abstract fun bindOptionalLogger(): Logger

    companion object {

        @OptIn(ExperimentalCoilApi::class)
        @Provides
        @Singleton
        fun provideImageLoader(
            @ApplicationContext appContext: Context,
            @Base okHttpClient: OkHttpClient,
            logger: Optional<Logger>,
        ): ImageLoader {
            return ImageLoader.Builder(appContext)
                .memoryCache {
                    MemoryCache.Builder(appContext)
                        // Set the max size to 15% of the app's available memory.
                        .maxSizePercent(0.15)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(appContext.filesDir.resolve("image_cache"))
                        .maxSizeBytes(512L * 1024 * 1024) // 512MB
                        .build()
                }
                .crossfade(true)
                .okHttpClient {
                    // Don't limit concurrent network requests by host.
                    val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                    okHttpClient
                        .newBuilder()
                        .dispatcher(dispatcher)
                        .build()
                }
                .apply { logger.ifPresent(this::logger) }
                .build()
        }
    }
}
