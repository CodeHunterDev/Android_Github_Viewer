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

package com.ferelin.githubviewer.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.githubviewer.App
import com.ferelin.githubviewer.R
import com.ferelin.githubviewer.databinding.FragmentSplashBinding
import com.ferelin.githubviewer.ui.search.SearchFragment
import com.ferelin.githubviewer.utils.CoroutineContextProvider
import com.ferelin.githubviewer.utils.TransitionManager
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SplashFragment : Fragment() {

    private val mViewModel: SplashViewModel by viewModels()
    private var mBinding: FragmentSplashBinding? = null

    @Inject
    protected lateinit var mCoroutineContext: CoroutineContextProvider

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
        mBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.initObservers()

        setUpViewComponents()
        initObservers()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun setUpViewComponents() {
        mBinding?.root?.addTransitionListener(object : TransitionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.container, SearchFragment())
                }
            }
        })
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.stateReady.collect { isReady ->
                withContext(mCoroutineContext.Main) {
                    if (isReady) {
                        mBinding!!.root.transitionToEnd()
                    }
                }
            }
        }
    }

    private fun injectDependencies() {
        val application = requireActivity().application
        if (application is App) {
            application.appComponent.inject(this)
        }
    }
}