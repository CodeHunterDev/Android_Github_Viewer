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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.ferelin.githubviewer.App
import com.ferelin.githubviewer.databinding.FragmentDownloadsBinding
import com.ferelin.githubviewer.ui.common.githubRepository.GithubRepositoryClickListener
import com.ferelin.githubviewer.ui.common.githubRepository.GithubRepositoryDecoration
import com.ferelin.githubviewer.utils.AnimationsRunner
import com.ferelin.githubviewer.utils.avoidNotFoundException
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class DownloadsFragment : Fragment(), GithubRepositoryClickListener {

    private val mViewModel: DownloadsViewModel by viewModels()
    private var mBinding: FragmentDownloadsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
        mViewModel.initObservers()

        enterTransition = MaterialFadeThrough().apply { duration = 300L }
        exitTransition = MaterialElevationScale(false).apply { duration = 200L }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewComponents()
        setUpClickListeners()
    }

    override fun onDestroyView() {
        AnimationsRunner.invalidateAnimations(sAnimationKey)
        mBinding?.recyclerViewDownloads?.adapter = null
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
        /**
         * Do nothing.
         * On this fragment already shows the downloaded repositories. Trying to download
         * again does not make sense
         * */
    }

    private fun setUpClickListeners() {
        mBinding?.let { binding ->
            binding.imageViewBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            binding.fab.setOnClickListener {
                AnimationsRunner.scrollToTopWithAnim(binding.recyclerViewDownloads, sAnimationKey)
            }
        }
    }

    private fun setUpViewComponents() {
        mBinding?.recyclerViewDownloads?.apply {
            mViewModel.repositoriesAdapter.setClickListener(this@DownloadsFragment)
            adapter = mViewModel.repositoriesAdapter
            addItemDecoration(GithubRepositoryDecoration(requireContext()))
        }
    }

    private fun injectDependencies() {
        val application = requireActivity().application
        if (application is App) {
            application.appComponent.inject(mViewModel)
        }
    }

    private companion object {
        /**
         * @see [AnimationsRunner]
         * */
        const val sAnimationKey = "downloads_fragment"
    }
}