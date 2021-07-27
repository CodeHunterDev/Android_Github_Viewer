/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.githubviewer.di.bindsModules

import com.ferelin.githubviewer.local.LocalManager
import com.ferelin.githubviewer.local.LocalManagerImpl
import com.ferelin.githubviewer.local.fileManager.FileManager
import com.ferelin.githubviewer.local.fileManager.FileManagerImpl
import com.ferelin.githubviewer.local.preferences.StorePreferences
import com.ferelin.githubviewer.local.preferences.StorePreferencesImpl
import com.ferelin.githubviewer.remote.ApiManager
import com.ferelin.githubviewer.remote.ApiManagerImpl
import com.ferelin.githubviewer.repository.Repository
import com.ferelin.githubviewer.repository.RepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BindModule {

    @Binds
    abstract fun provideLocalManager(localManagerImpl: LocalManagerImpl): LocalManager

    @Binds
    abstract fun provideApiManager(apiManagerImpl: ApiManagerImpl): ApiManager

    @Binds
    abstract fun provideStorePreferences(
        storePreferencesImpl: StorePreferencesImpl
    ): StorePreferences

    @Binds
    abstract fun provideFileManager(fileManagerImpl: FileManagerImpl): FileManager

    @Binds
    abstract fun provideRepository(repository: RepositoryImpl) : Repository
}