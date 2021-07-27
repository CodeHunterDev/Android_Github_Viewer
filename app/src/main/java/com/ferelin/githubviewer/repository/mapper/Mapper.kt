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

package com.ferelin.githubviewer.repository.mapper

import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.remote.ApiManager
import com.ferelin.githubviewer.remote.api.BaseResponse
import com.ferelin.githubviewer.remote.api.userRepositories.LoadRepositoryResponse
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Mapper] converts data
 * */
@Singleton
class Mapper @Inject constructor() {

    fun mapRepositoriesResponseToGithubRepositories(
        networkResponse: BaseResponse<List<LoadRepositoryResponse>>
    ): List<GithubRepository> {
        return if (networkResponse.responseCode == ApiManager.RESPONSE_OK
            && networkResponse.responseData != null
        ) {
            networkResponse.responseData.map { remoteResponse ->
                GithubRepository(
                    id = remoteResponse.repositoryId,
                    ownerLogin = remoteResponse.owner.ownerLogin,
                    ownerAvatarUrl = remoteResponse.owner.ownerAvatarUrl,
                    projectName = remoteResponse.projectName,
                    projectDescription = remoteResponse.projectDescription ?: "",
                    projectUrl = remoteResponse.projectUrl,
                    projectStarsCount = remoteResponse.projectStarsCount
                )
            }
        } else emptyList()
    }

    fun unpackInputStreamResponse(response: BaseResponse<InputStream>?): InputStream? {
        return if (response != null
            && response.responseCode == ApiManager.RESPONSE_OK
            && response.responseData != null
        ) {
            response.responseData
        } else {
            null
        }
    }
}