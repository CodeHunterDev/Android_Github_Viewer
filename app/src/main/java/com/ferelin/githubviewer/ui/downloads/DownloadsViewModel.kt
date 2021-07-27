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

package com.ferelin.githubviewer.ui.downloads

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.repository.Repository
import com.ferelin.githubviewer.ui.common.githubRepository.adapter.GithubRepositoryAdapter
import com.ferelin.githubviewer.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    protected lateinit var mRepository: Repository

    @Inject
    protected lateinit var mCoroutineContext: CoroutineContextProvider

    private var mWasInitialized: Boolean = false

    val repositoriesAdapter = GithubRepositoryAdapter().apply {
        setHasStableIds(true)
    }

    fun initObservers() {
        if (!mWasInitialized) {
            mWasInitialized = true

            viewModelScope.launch(mCoroutineContext.IO) {
                /**
                 * First gets already cached repositories
                 * */
                val loadedRepositories = mRepository.getLoadedRepositoriesFromCache()
                setDataToAdapter(loadedRepositories)

                /**
                 * Next listens for new repositories at local database
                 * */
                mRepository.collectLoadedRepositoriesUpdates().collect { cachedRepositories ->
                    if (cachedRepositories.isNotEmpty()) {
                        val actualData = repositoriesAdapter.githubRepositories
                        cachedRepositories.forEach {
                            /**
                             * Checks for avoid repositories duplicates at adapter
                             * */
                            if (!actualData.contains(it)) {
                                withContext(mCoroutineContext.Main) {
                                    repositoriesAdapter.addItem(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun setDataToAdapter(items: List<GithubRepository>) {
        withContext(mCoroutineContext.Main) {
            repositoriesAdapter.setData(items)
        }
    }
}