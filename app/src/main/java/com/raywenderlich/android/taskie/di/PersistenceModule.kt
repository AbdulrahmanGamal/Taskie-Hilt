package com.raywenderlich.android.taskie.di

import com.raywenderlich.android.taskie.preferences.PreferencesHelper
import com.raywenderlich.android.taskie.preferences.PreferencesHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract  class PersistenceModule {
    @Binds
    @Singleton
    abstract fun bindPreferencesHelper(preferencesHelperImpl: PreferencesHelperImpl):PreferencesHelper
}