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

package com.ferelin.githubviewer.ui.common.githubRepository.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.githubviewer.R
import com.ferelin.githubviewer.databinding.ItemGithubRepositoryBinding
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository

class GithubRepositoryViewHolder(
    val binding: ItemGithubRepositoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(githubRepository: GithubRepository) {
        bindData(githubRepository)

        val context = binding.root.context
        val errorIcon = AppCompatResources.getDrawable(context, R.drawable.ic_load_error)

        Glide
            .with(binding.root)
            .load(githubRepository.ownerAvatarUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(errorIcon)
            .into(binding.imageViewAvatar)

    }

    private fun bindData(githubRepository: GithubRepository) {
        binding.apply {
            textViewOwnerLogin.text = githubRepository.ownerLogin
            textViewProjectName.text = githubRepository.projectName
            textViewDescription.text = githubRepository.projectDescription
            textViewStarsCount.text = githubRepository.projectStarsCount.toString()
        }
    }

    companion object {
        fun from(parent: ViewGroup): GithubRepositoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemGithubRepositoryBinding.inflate(inflater, parent, false)
            return GithubRepositoryViewHolder(binding)
        }
    }
}