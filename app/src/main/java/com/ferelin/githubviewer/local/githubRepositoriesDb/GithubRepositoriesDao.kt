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

package com.ferelin.githubviewer.local.githubRepositoriesDb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GithubRepositoriesDao {

    /**
     * [OnConflictStrategy.ABORT] is used to avoid github local path overwrite
     * */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun cacheRepository(githubRepository: GithubRepository)

    @Update
    suspend fun updateRepositoryCache(githubRepository: GithubRepository)

    @Query("SELECT * FROM `githubviewer.repositories.db` WHERE id=:id")
    fun getRepositoryById(id: Long): GithubRepository?

    @Query("SELECT * FROM `githubviewer.repositories.db` WHERE localPath IS NOT NULL")
    fun collectLoadedRepositoriesUpdates(): Flow<List<GithubRepository>>

    @Query("SELECT * FROM `githubviewer.repositories.db` WHERE localPath IS NOT NULL")
    suspend fun getLoadedRepositoriesFromCache(): List<GithubRepository>

    @Query("SELECT * FROM `githubviewer.repositories.db` WHERE ownerLogin=:ownerLogin")
    suspend fun getCachedRepositoriesByOwnerLogin(ownerLogin: String): List<GithubRepository>?
}