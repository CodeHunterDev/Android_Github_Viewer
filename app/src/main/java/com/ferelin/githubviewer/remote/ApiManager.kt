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

package com.ferelin.githubviewer.remote

import com.ferelin.githubviewer.local.LocalManager
import com.ferelin.githubviewer.remote.api.BaseResponse
import com.ferelin.githubviewer.remote.api.userRepositories.LoadRepositoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

/**
 * [ApiManager] is used for interacting with remote-based entities
 * */
interface ApiManager {

    /**
     * Sends request for repository downloading.
     * @return [Flow] with packed [BaseResponse] which hold input data stream of .zip file,
     * otherwise returns packed [BaseResponse] with error code
     * */
    fun downloadRepository(
        username: String,
        projectName: String
    ): Flow<BaseResponse<InputStream>>

    fun loadUserRepositories(username: String): BaseResponse<List<LoadRepositoryResponse>>

    companion object {
        const val BASE_URL = "https://api.github.com/"

        /**
         * Response constants to determine the status of the response in repository
         * */
        const val RESPONSE_OK = 200
        const val RESPONSE_NO_DATA = 429
        const val RESPONSE_UNDEFINED = 430
    }
}