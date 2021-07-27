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

package com.ferelin.githubviewer.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.githubviewer.R

object AnimationsRunner {

    private val mAnimationsContainer = HashMap<String, MutableList<Animation>>(10)

    /**
     * Position in recyclerView after which need to apply another type of animation
     * */
    private const val mConsiderItemIsAway = 13

    /**
     * Scrolls [RecyclerView] view to top with custom animation.
     * @param recyclerView is a target recycler view that must be scrolled to top
     * @param key a key by which animations can be stopped
     * */
    fun scrollToTopWithAnim(recyclerView: RecyclerView, key: String) {
        val layoutManager = recyclerView.layoutManager

        if (layoutManager is LinearLayoutManager
            && layoutManager.findFirstVisibleItemPosition() < mConsiderItemIsAway
        ) {
            recyclerView.smoothScrollToPosition(0)
            return
        }

        /**
         * RecyclerView fade out
         * -> hard scroll to [mConsiderItemIsAway] - 5
         * -> fadeIn
         * -> smooth scroll to 0
         * */
        val fadeInCallback = object : AnimationManager() {
            override fun onAnimationStart(animation: Animation?) {
                recyclerView.alpha = 1F
                recyclerView.smoothScrollToPosition(0)
            }
        }

        val fadeOutCallback = object : AnimationManager() {
            override fun onAnimationStart(animation: Animation?) {
                recyclerView.smoothScrollBy(
                    0,
                    -recyclerView.height
                )
            }

            override fun onAnimationEnd(animation: Animation?) {
                recyclerView.alpha = 0F
                recyclerView.scrollToPosition(mConsiderItemIsAway - 5)
                runFadeInAnimation(recyclerView, fadeInCallback, key)
            }
        }
        runFadeOutAnimation(recyclerView, fadeOutCallback, key)
    }

    /**
     * @see scrollToTopWithAnim
     * */
    fun invalidateAnimations(key: String) {
        mAnimationsContainer[key]?.let { animationsList ->
            animationsList.forEach { it.invalidate() }
        }
        mAnimationsContainer[key] = mutableListOf()
    }

    private fun runFadeInAnimation(
        target: View,
        listener: Animation.AnimationListener? = null,
        key: String
    ) {
        val fadeIn = AnimationUtils.loadAnimation(target.context, R.anim.fade_in)
        fadeIn.setAnimationListener(listener)
        target.startAnimation(fadeIn)

        addAnimToContainer(fadeIn, key)
    }

    private fun runFadeOutAnimation(
        target: View,
        listener: Animation.AnimationListener? = null,
        key: String
    ) {
        val fadeOut = AnimationUtils.loadAnimation(target.context, R.anim.fade_out)
        fadeOut.setAnimationListener(listener)
        target.startAnimation(fadeOut)

        addAnimToContainer(fadeOut, key)
    }

    private fun addAnimToContainer(animation: Animation, key: String) {
        val animationsList = mAnimationsContainer[key]
        if (animationsList == null) {
            mAnimationsContainer[key] = mutableListOf(animation)
        } else animationsList.add(animation)
    }
}