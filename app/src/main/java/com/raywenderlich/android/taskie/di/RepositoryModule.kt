package com.raywenderlich.android.taskie.di

import com.raywenderlich.android.taskie.networking.RemoteApi
import com.raywenderlich.android.taskie.networking.RemoteApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRemoteApi(remoteApiImpl: RemoteApiImpl):RemoteApi
}