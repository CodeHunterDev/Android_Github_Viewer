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

import com.ferelin.githubviewer.remote.api.BaseResponse
import com.ferelin.githubviewer.remote.api.downloadRepository.DownloadRepositoryApi
import com.ferelin.githubviewer.remote.api.userRepositories.LoadRepositoriesApi
import com.ferelin.githubviewer.remote.api.userRepositories.LoadRepositoryResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiManagerImpl @Inject constructor(
    private val mDownloadRepositoryApi: DownloadRepositoryApi,
    private val mLoadRepositoriesApi: LoadRepositoriesApi
) : ApiManager {

    override fun downloadRepository(
        username: String,
        projectName: String
    ): Flow<BaseResponse<InputStream>> = callbackFlow {
        mDownloadRepositoryApi
            .downloadRepository(username, projectName)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        trySend(
                            BaseResponse.createResponse(
                                responseBody = responseBody.byteStream(),
                                responseCode = ApiManager.RESPONSE_OK
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    trySend(
                        BaseResponse.failed()
                    )
                }
            })
        awaitClose()
    }

    override fun loadUserRepositories(
        username: String
    ): BaseResponse<List<LoadRepositoryResponse>> {
        return try {
            val networkResponse = mLoadRepositoriesApi
                .loadUserRepositories(username)
                .execute()
            BaseResponse.createResponse(networkResponse.body(), networkResponse.code())
        } catch (exception: SocketTimeoutException) {
            BaseResponse.failed()
        }
    }
}