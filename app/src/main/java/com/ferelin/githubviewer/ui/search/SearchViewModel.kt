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

package com.ferelin.githubviewer.ui.search

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.repository.Repository
import com.ferelin.githubviewer.ui.common.githubRepository.adapter.GithubRepositoryAdapter
import com.ferelin.githubviewer.utils.CoroutineContextProvider
import com.ferelin.githubviewer.utils.DataNotificator
import com.ferelin.githubviewer.utils.Messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

class SearchViewModel : ViewModel() {

    @Inject
    protected lateinit var mRepository: Repository

    @Inject
    protected lateinit var mAppScope: CoroutineScope

    @Inject
    protected lateinit var mCoroutineContext: CoroutineContextProvider

    /**
     * [StateFlow] that notifies the current state of the request (loading or not) for UI updates.
     * */
    private val mStateSearchForData = MutableStateFlow(DataNotificator.NONE)
    val stateSearchForData: StateFlow<DataNotificator>
        get() = mStateSearchForData.asStateFlow()

    private val mEventMessages = MutableSharedFlow<Messages>()
    val eventMessages: SharedFlow<Messages>
        get() = mEventMessages.asSharedFlow()

    private val mEventAskForPath = MutableSharedFlow<Unit>()
    val eventAskForPath: SharedFlow<Unit>
        get() = mEventAskForPath.asSharedFlow()

    /**
     * Defines for which user the data is currently loaded at UI.
     * Used to determine whether new elements need to be added by one or whether adapter data
     * can be full replaced by new.
     * */
    private var mDataIsSetFor: String = ""

    private var mLastInputSearch: String = ""
    private var mTimer: Timer? = null

    /**
     * If user refused to select the path to save the file, then the last download request will
     * be saved to this variable and will be repeated when path will be selected.
     * */
    private var mRepositoryPendingDownload: GithubRepository? = null

    val repositoriesAdapter = GithubRepositoryAdapter().apply {
        setHasStableIds(true)
    }

    fun onSearchUserLoginChanged(userLogin: String) {
        if (userLogin == mLastInputSearch || userLogin.isEmpty()) {
            return
        }

        mLastInputSearch = userLogin

        // To avoid fast input
        mTimer?.cancel()
        mTimer = Timer().apply {
            schedule(timerTask {
                loadRepositoriesByUserLogin(mLastInputSearch)
            }, 1000)
        }
    }

    fun onDownloadIconClicked(position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val clickedGithubRepository = repositoriesAdapter.githubRepositories[position]
            val cachedRepository = mRepository.getRepositoryById(clickedGithubRepository.id)

            /**
             * Checks if repository is not already downloaded
             * */
            if (cachedRepository == null || cachedRepository.localPath != null) {
                mEventMessages.emit(Messages.ALREADY_DOWNLOADED)
                return@launch
            }

            val storagePath = mRepository.getSelectedStoragePath()
            val pathAuthority = mRepository.getStoragePathAuthority()

            if (storagePath == null || pathAuthority == null) {
                mRepositoryPendingDownload = clickedGithubRepository
                mEventAskForPath.emit(Unit)
            } else {
                downloadRepository(clickedGithubRepository, storagePath, pathAuthority)
            }
        }
    }

    fun onNewValidUriWithPathGot(uri: Uri) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val newPath = uri.path!!
            val newPathAuthority = uri.authority!!

            mAppScope.launch {
                mRepository.cacheSelectedStoragePath(newPath)
                mRepository.cacheStoragePathAuthority(newPathAuthority)
            }

            val waitingForDownload = mRepositoryPendingDownload
            if (waitingForDownload != null) {
                downloadRepository(
                    waitingForDownload,
                    newPath,
                    newPathAuthority
                )
                mRepositoryPendingDownload = null
            }
        }
    }

    private fun loadRepositoriesByUserLogin(userLogin: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mStateSearchForData.value = DataNotificator.LOADING

            /**
             * First loads repositories for [userLogin] from database.
             * */
            val repositoriesFromCache = mRepository.getCachedRepositoriesByOwnerLogin(userLogin)
            if (repositoriesFromCache != null && repositoriesFromCache.isNotEmpty()) {
                mStateSearchForData.value = DataNotificator.PREPARED
                mDataIsSetFor = userLogin
                setDataToAdapter(repositoriesFromCache)
            }

            val loadedRepositories = try {
                mRepository.loadUserRepositories(userLogin)
            } catch (unknownHost: UnknownHostException) {
                mEventMessages.emit(Messages.UNKNOWN_HOST)
                emptyList()
            }

            // For duplicate checks
            val actualDataBeforeSet = repositoriesAdapter.githubRepositories

            if (loadedRepositories.isEmpty() && repositoriesFromCache?.isEmpty() == true) {
                mStateSearchForData.value = DataNotificator.NONE
                mDataIsSetFor = ""
                setDataToAdapter(emptyList())
                return@launch
            }

            if (mDataIsSetFor != userLogin) {
                mStateSearchForData.value = DataNotificator.PREPARED
                setDataToAdapter(loadedRepositories)

                mAppScope.launch {
                    loadedRepositories.forEach { loadedRepository -> cacheToDb(loadedRepository) }
                }
            } else {
                loadedRepositories.forEach { loadedRepository ->
                    // Checks for duplicates
                    if (!actualDataBeforeSet.contains(loadedRepository)) {
                        withContext(this@SearchViewModel.mCoroutineContext.Main) {
                            repositoriesAdapter.addItem(loadedRepository)
                        }

                        mAppScope.launch { cacheToDb(loadedRepository) }
                    }
                }
            }

            mDataIsSetFor = userLogin
        }
    }

    private suspend fun downloadRepository(
        repository: GithubRepository,
        storagePath: String,
        authorityPath: String
    ) {
        mEventMessages.emit(Messages.DOWNLOAD_STARTED)

        mRepository.downloadRepository(repository)?.let { receivedDataStream ->
            mAppScope.launch {
                mRepository.cacheZipToSelectedPath(
                    inputStreamData = receivedDataStream,
                    treePath = storagePath,
                    pathAuthority = authorityPath,
                    fileName = "${repository.projectName}_${repository.id}"
                )?.let { savedByPath ->
                    mEventMessages.emit(Messages.DOWNLOAD_FINISHED)
                    repository.localPath = savedByPath
                    mRepository.updateRepositoryCache(repository)
                } ?: mEventMessages.emit(Messages.FILE_CACHE_FAILED)
            }
        } ?: mEventMessages.emit(Messages.DOWNLOAD_FAILED)
    }

    private suspend fun setDataToAdapter(githubRepositories: List<GithubRepository>) {
        withContext(mCoroutineContext.Main) {
            repositoriesAdapter.setData(githubRepositories)
        }
    }

    private suspend fun cacheToDb(item: GithubRepository) {
        /**
         * At room database is used OnConflictStrategy.ABORT that throws
         * [SQLiteConstraintException] if item is already exists.
         * */
        try {
            mRepository.cacheRepository(item)
        } catch (exception: SQLiteConstraintException) {
            // Do nothing. Ignore item
        }
    }
}