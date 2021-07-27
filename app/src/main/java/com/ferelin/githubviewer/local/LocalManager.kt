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
import com.ferelin.githubviewer.local.fileManager.FileManagerImpl
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepositoriesDao
import com.ferelin.githubviewer.local.preferences.StorePreferences

/**
 * [LocalManager] is used for interacting with local-based entities
 * */
interface LocalManager : GithubRepositoriesDao, StorePreferences, FileManager