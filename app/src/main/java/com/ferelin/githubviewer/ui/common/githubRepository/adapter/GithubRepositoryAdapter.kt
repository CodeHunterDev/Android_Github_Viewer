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

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.githubviewer.local.githubRepositoriesDb.GithubRepository
import com.ferelin.githubviewer.ui.common.githubRepository.GithubRepositoryClickListener

class GithubRepositoryAdapter(
    private var mGithubRepositoryClickListener: GithubRepositoryClickListener? = null
) : RecyclerView.Adapter<GithubRepositoryViewHolder>() {

    private var mGithubRepositories = ArrayList<GithubRepository>()
    val githubRepositories: List<GithubRepository>
        get() = mGithubRepositories.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubRepositoryViewHolder {
        return GithubRepositoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GithubRepositoryViewHolder, position: Int) {
        holder.bind(mGithubRepositories[position])

        holder.binding.textViewOpenInBrowser.setOnClickListener {
            mGithubRepositoryClickListener?.onUrlClicked(position)
        }
        holder.binding.imageViewDownload.setOnClickListener {
            mGithubRepositoryClickListener?.onDownloadClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return mGithubRepositories.size
    }

    override fun getItemId(position: Int): Long {
        return mGithubRepositories[position].id
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(githubRepositories: List<GithubRepository>) {
        mGithubRepositories = ArrayList(githubRepositories)
        notifyDataSetChanged()
    }

    fun addItem(githubRepository: GithubRepository) {
        mGithubRepositories.add(0, githubRepository)
        notifyItemInserted(0)
    }

    fun setClickListener(githubRepositoryClickListener: GithubRepositoryClickListener) {
        mGithubRepositoryClickListener = githubRepositoryClickListener
    }
}