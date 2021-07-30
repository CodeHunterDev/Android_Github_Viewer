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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.githubviewer.App
import com.ferelin.githubviewer.R
import com.ferelin.githubviewer.databinding.FragmentSearchBinding
import com.ferelin.githubviewer.ui.common.githubRepository.GithubRepositoryClickListener
import com.ferelin.githubviewer.ui.common.githubRepository.GithubRepositoryDecoration
import com.ferelin.githubviewer.ui.downloads.DownloadsFragment
import com.ferelin.githubviewer.utils.*
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchFragment : Fragment(), GithubRepositoryClickListener {

    private val mViewModel: SearchViewModel by viewModels()
    private var mBinding: FragmentSearchBinding? = null

    @Inject
    lateinit var mCoroutineContext: CoroutineContextProvider

    private val mAskForPath = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { resultUri ->
        if (resultUri.path != null && resultUri.authority != null) {
            mViewModel.onNewValidUriWithPathGot(resultUri)
        } else showError(requireContext(), getString(R.string.notificationPathNotSelected))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()

        enterTransition = MaterialFadeThrough().apply { duration = 300L }
        exitTransition = MaterialElevationScale(false).apply { duration = 200L }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewComponents()
        setUpClickListeners()
        initObservers()
    }

    override fun onDestroyView() {
        AnimationsRunner.invalidateAnimations(sAnimationsKey)
        mBinding?.recyclerViewRepositories?.adapter = null
        mBinding = null
        super.onDestroyView()
    }

    override fun onUrlClicked(position: Int) {
        avoidNotFoundException(requireContext()) {
            val url = mViewModel.repositoriesAdapter.githubRepositories[position].projectUrl
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }

    override fun onDownloadClicked(position: Int) {
        mViewModel.onDownloadIconClicked(position)
    }

    private fun injectDependencies() {
        val application = requireActivity().application
        if (application is App) {
            application.appComponent.inject(this)
            application.appComponent.inject(mViewModel)
        }
    }

    private fun setUpViewComponents() {
        mBinding?.recyclerViewRepositories?.apply {
            adapter = mViewModel.repositoriesAdapter
            mViewModel.repositoriesAdapter.setClickListener(this@SearchFragment)
            addItemDecoration(GithubRepositoryDecoration(requireContext()))
        }
    }

    private fun setUpClickListeners() {
        mBinding?.let { binding ->
            binding.editTextSearch.addTextChangedListener {
                mViewModel.onSearchUserLoginChanged(it?.toString() ?: "")
            }

            binding.imageViewFolder.setOnClickListener {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.container, DownloadsFragment())
                    addToBackStack(null)
                }
            }

            binding.fab.setOnClickListener {
                AnimationsRunner.scrollToTopWithAnim(
                    binding.recyclerViewRepositories,
                    sAnimationsKey
                )
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectEventAskForPath() }
            launch { collectEventMessages() }
            launch { collectStateSearchForData() }
        }
    }

    private suspend fun collectEventAskForPath() {
        mViewModel.eventAskForPath.collect {
            avoidNotFoundException(requireContext()) { mAskForPath.launch(Uri.EMPTY) }
        }
    }

    private suspend fun collectStateSearchForData() {
        mViewModel.stateSearchForData.collect { notificator ->
            withContext(mCoroutineContext.Main) {
                when (notificator) {
                    DataNotificator.NONE -> {
                        mBinding!!.circularProgressBar.isVisible = false
                        mBinding!!.textViewEmptyList.isVisible = true
                    }
                    DataNotificator.PREPARED -> {
                        mBinding!!.circularProgressBar.isVisible = false
                        mBinding!!.textViewEmptyList.isVisible = false
                    }
                    DataNotificator.LOADING -> {
                        mBinding!!.textViewEmptyList.isVisible = false
                        mBinding!!.circularProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    private suspend fun collectEventMessages() {
        mViewModel.eventMessages.collect { message ->
            val notificationText = when (message) {
                Messages.DOWNLOAD_FINISHED -> getString(R.string.notificationDownloadFinished)
                Messages.DOWNLOAD_STARTED -> getString(R.string.notificationDownloadStarted)
                Messages.DOWNLOAD_FAILED -> getString(R.string.notificationDownloadFailed)
                Messages.UNKNOWN_HOST -> getString(R.string.notificationUnknownHost)
                Messages.ALREADY_DOWNLOADED -> getString(R.string.notificationAlreadyDownloaded)
                Messages.FILE_CACHE_FAILED -> getString(R.string.notificationFileCacheFailed)
            }

            withContext(mCoroutineContext.Main) { showError(requireContext(), notificationText) }
        }
    }

    private companion object {
        /**
         * @see [AnimationsRunner]
         * */
        const val sAnimationsKey = "search_fragment"
    }
}