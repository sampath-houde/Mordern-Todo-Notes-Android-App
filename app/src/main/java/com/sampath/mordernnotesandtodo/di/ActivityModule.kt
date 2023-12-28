package com.sampath.mordernnotesandtodo.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    fun providesContext(@ActivityContext context: ActivityContext) = context
}