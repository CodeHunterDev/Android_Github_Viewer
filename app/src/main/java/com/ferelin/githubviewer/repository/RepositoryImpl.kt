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

package com.ferelin.githubviewer.repository

import com.ferelin.githubviewer.local.LocalManager
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.remote.ApiManager
import com.ferelin.githubviewer.repository.inconsistencyDetector.InconsistencyDetector
import com.ferelin.githubviewer.repository.mapper.Mapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val mApiManager: ApiManager,
    private val mLocalManager: LocalManager,
    private val mMapper: Mapper,
    private val mInconsistencyDetector: InconsistencyDetector
) : Repository {

    override suspend fun cacheSelectedStoragePath(storagePath: String) {
        mLocalManager.cacheSelectedStoragePath(storagePath)
    }

    override suspend fun getSelectedStoragePath(): String? {
        return mLocalManager.getSelectedStoragePath()
    }

    override suspend fun cacheStoragePathAuthority(authority: String) {
        mLocalManager.cacheStoragePathAuthority(authority)
    }

    override suspend fun getStoragePathAuthority(): String? {
        return mLocalManager.getStoragePathAuthority()
    }

    override suspend fun cacheRepository(githubRepository: GithubRepository) {
        mLocalManager.cacheRepository(githubRepository)
    }

    override fun getRepositoryById(id: Long): GithubRepository? {
        return mLocalManager.getRepositoryById(id)
    }

    override suspend fun updateRepositoryCache(githubRepository: GithubRepository) {
        mLocalManager.updateRepositoryCache(githubRepository)
    }

    override fun collectLoadedRepositoriesUpdates(): Flow<List<GithubRepository>> {
        return mLocalManager.collectLoadedRepositoriesUpdates()
    }

    override suspend fun getLoadedRepositoriesFromCache(): List<GithubRepository> {
        val loadedRepositories = mLocalManager.getLoadedRepositoriesFromCache()
        return mInconsistencyDetector.detectInconsistencyAndFix(loadedRepositories)
    }

    override suspend fun getCachedRepositoriesByOwnerLogin(ownerLogin: String): List<GithubRepository>? {
        return mLocalManager.getCachedRepositoriesByOwnerLogin(ownerLogin)
    }

    override suspend fun downloadRepository(
        sourceGithubRepository: GithubRepository
    ): InputStream? {
        val networkResponse = mApiManager.downloadRepository(
            sourceGithubRepository.ownerLogin,
            sourceGithubRepository.projectName
        ).firstOrNull()
        return mMapper.unpackInputStreamResponse(networkResponse)
    }

    override fun loadUserRepositories(username: String): List<GithubRepository> {
        val networkResponse = mApiManager.loadUserRepositories(username)
        return mMapper.mapRepositoriesResponseToGithubRepositories(networkResponse)
    }

    override fun cacheZipToSelectedPath(
        inputStreamData: InputStream,
        treePath: String,
        pathAuthority: String,
        fileName: String
    ): String? {
        return mLocalManager.cacheZipToSelectedPath(
            inputStreamData,
            treePath,
            pathAuthority,
            fileName
        )
    }
}