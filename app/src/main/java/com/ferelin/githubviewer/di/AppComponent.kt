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

package com.ferelin.githubviewer.di

import android.content.Context
import com.ferelin.githubviewer.di.bindsModules.BindModule
import com.ferelin.githubviewer.di.modules.AppModule
import com.ferelin.githubviewer.di.modules.LocalModule
import com.ferelin.githubviewer.di.modules.RemoteModule
import com.ferelin.githubviewer.ui.downloads.DownloadsFragment
import com.ferelin.githubviewer.ui.downloads.DownloadsViewModel
import com.ferelin.githubviewer.ui.search.SearchFragment
import com.ferelin.githubviewer.ui.search.SearchViewModel
import com.ferelin.githubviewer.ui.splash.SplashFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        LocalModule::class,
        RemoteModule::class,
        BindModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(searchViewModel: SearchViewModel)
    fun inject(downloadsViewModel: DownloadsViewModel)

    fun inject(searchFragment: SearchFragment)
    fun inject(splashFragment: SplashFragment)
}