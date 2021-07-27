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

package com.ferelin.githubviewer.repository.inconsistencyDetector

import com.ferelin.githubviewer.local.LocalManager
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [InconsistencyDetector] provides ability to detect inconsistency between data
 * */
@Singleton
class InconsistencyDetector @Inject constructor(
    private val mLocalManager: LocalManager
) {
    /**
     *  Checks that files that have a path are still actually on the device.
     *  If it is not, then it removes the path from the object and caches updated item.
     *  @return [List] with [GithubRepository] type in which all objects are with local path
     *  and still are actually on device.
     * */
    suspend fun detectInconsistencyAndFix(
        githubRepositories: List<GithubRepository>
    ): List<GithubRepository> {
        return githubRepositories
            .onEach { githubRepository ->
                val loadedFile = File(githubRepository.localPath!!)
                if (!loadedFile.exists()) {
                    githubRepository.localPath = null
                    mLocalManager.updateRepositoryCache(githubRepository)
                }
            }
            .filter { it.localPath != null }
    }
}