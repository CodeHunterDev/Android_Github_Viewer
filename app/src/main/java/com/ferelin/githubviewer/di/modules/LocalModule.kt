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

@file:Suppress("SameParameterValue")

package com.ferelin.githubviewer.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepositoriesDao
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepositoriesDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Provides
    fun provideGithubRepositoriesDbDao(
        githubRepositoriesDb: GithubRepositoriesDb
    ): GithubRepositoriesDao {
        return githubRepositoriesDb.githubRepositoriesDao()
    }

    @Provides
    @Singleton
    fun provideGithubRepositoriesDb(context: Context): GithubRepositoriesDb {
        return buildDatabase(
            context,
            GithubRepositoriesDb::class.java,
            GithubRepositoriesDb.DB_NAME
        )
    }

    private fun <T : RoomDatabase> buildDatabase(
        context: Context,
        klass: Class<T>,
        dbName: String
    ): T {
        return Room.databaseBuilder(
            context,
            klass,
            dbName
        ).fallbackToDestructiveMigration().build()
    }
}