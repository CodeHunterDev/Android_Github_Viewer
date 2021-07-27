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

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorePreferencesImpl @Inject constructor(
    private val mContext: Context
) : StorePreferences {

    private val Context.dataStorePreferences by preferencesDataStore(name = "githubviewer.preferences.db")

    private val mSelectedStoragePathKey = stringPreferencesKey("storage_path")
    private val mStoragePathAuthorityKey = stringPreferencesKey("path_authority")

    override suspend fun cacheSelectedStoragePath(storagePath: String) {
        mContext.dataStorePreferences.edit {
            it[mSelectedStoragePathKey] = storagePath
        }
    }

    override suspend fun getSelectedStoragePath(): String? {
        return mContext.dataStorePreferences.data.map {
            it[mSelectedStoragePathKey]
        }.firstOrNull()
    }

    override suspend fun cacheStoragePathAuthority(authority: String) {
        mContext.dataStorePreferences.edit {
            it[mStoragePathAuthorityKey] = authority
        }
    }

    override suspend fun getStoragePathAuthority(): String? {
        return mContext.dataStorePreferences.data.map {
            it[mStoragePathAuthorityKey]
        }.firstOrNull()
    }
}