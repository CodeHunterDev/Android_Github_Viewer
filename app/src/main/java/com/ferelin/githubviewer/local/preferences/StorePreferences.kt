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

package com.ferelin.githubviewer.local.preferences

interface StorePreferences {

    /**
     * Caches storage path that was selected by user.
     * In the future it is used to avoid repeated requests to the user
     * */
    suspend fun cacheSelectedStoragePath(storagePath: String)

    suspend fun getSelectedStoragePath() : String?

    /**
     * Caches storage path authority that was selected by user.
     * In the future it is used to avoid repeated requests to the user
     * */
    suspend fun cacheStoragePathAuthority(authority: String)

    suspend fun getStoragePathAuthority() : String?
}