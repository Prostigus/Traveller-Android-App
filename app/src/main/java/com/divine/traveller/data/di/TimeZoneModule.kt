package com.divine.traveller.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.iakovlev.timeshape.TimeZoneEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeZoneModule {

    private val engineDeferred: Deferred<TimeZoneEngine> by lazy {
        CoroutineScope(Dispatchers.Default).async {
            TimeZoneEngine.initialize()
        }
    }

    @Provides
    @Singleton
    fun provideTimeZoneEngineDeferred(): Deferred<TimeZoneEngine> = engineDeferred
}