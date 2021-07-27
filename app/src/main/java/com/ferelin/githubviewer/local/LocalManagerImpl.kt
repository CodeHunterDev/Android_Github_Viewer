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

package com.ferelin.githubviewer.local

import com.ferelin.githubviewer.local.fileManager.FileManager
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepositoriesDao
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.local.preferences.StorePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalManagerImpl @Inject constructor(
    private val mGithubRepositoriesDao: GithubRepositoriesDao,
    private val mStorePreferences: StorePreferences,
    private var mFileManager: FileManager
) : LocalManager {

    override suspend fun cacheRepository(githubRepository: GithubRepository) {
        mGithubRepositoriesDao.cacheRepository(githubRepository)
    }

    override suspend fun updateRepositoryCache(githubRepository: GithubRepository) {
        mGithubRepositoriesDao.updateRepositoryCache(githubRepository)
    }

    override fun collectLoadedRepositoriesUpdates(): Flow<List<GithubRepository>> {
        return mGithubRepositoriesDao.collectLoadedRepositoriesUpdates().distinctUntilChanged()
    }

    override suspend fun getLoadedRepositoriesFromCache(): List<GithubRepository> {
        return mGithubRepositoriesDao.getLoadedRepositoriesFromCache()
    }

    override suspend fun getCachedRepositoriesByOwnerLogin(ownerLogin: String): List<GithubRepository>? {
        return mGithubRepositoriesDao.getCachedRepositoriesByOwnerLogin(ownerLogin)
    }

    override fun getRepositoryById(id: Long): GithubRepository? {
        return mGithubRepositoriesDao.getRepositoryById(id)
    }

    override suspend fun cacheSelectedStoragePath(storagePath: String) {
        mStorePreferences.cacheSelectedStoragePath(storagePath)
    }

    override suspend fun getSelectedStoragePath(): String? {
        return mStorePreferences.getSelectedStoragePath()
    }

    override suspend fun cacheStoragePathAuthority(authority: String) {
        mStorePreferences.cacheStoragePathAuthority(authority)
    }

    override suspend fun getStoragePathAuthority(): String? {
        return mStorePreferences.getStoragePathAuthority()
    }

    override fun cacheZipToSelectedPath(
        inputStreamData: InputStream,
        treePath: String,
        pathAuthority: String,
        fileName: String
    ): String? {
        return mFileManager.cacheZipToSelectedPath(
            inputStreamData,
            treePath,
            pathAuthority,
            fileName
        )
    }
}