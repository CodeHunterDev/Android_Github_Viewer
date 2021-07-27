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

package com.ferelin.githubviewer.remote.api

import com.ferelin.githubviewer.remote.ApiManager

class BaseResponse<T>(
    val responseCode: Int? = null,
    val responseData: T? = null
) {
    companion object {
        fun <T> failed(): BaseResponse<T> {
            return BaseResponse(responseCode = ApiManager.RESPONSE_UNDEFINED)
        }

        fun <T> createResponse(responseBody: T?, responseCode: Int): BaseResponse<T> {
            return when {
                responseCode == 200 -> {
                    BaseResponse(
                        responseCode = ApiManager.RESPONSE_OK,
                        responseData = responseBody
                    )
                }
                responseBody == null -> BaseResponse(ApiManager.RESPONSE_NO_DATA)
                else -> failed()
            }
        }
    }
}