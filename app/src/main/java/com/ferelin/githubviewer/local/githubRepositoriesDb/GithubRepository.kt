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

package com.ferelin.githubviewer.local.githubRepositoriesDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = GithubRepositoriesDb.DB_NAME)
data class GithubRepository(
    @PrimaryKey
    val id: Long,

    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val projectName: String,
    val projectUrl: String,
    val projectDescription: String,
    val projectStarsCount: Long,
    var localPath: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other is GithubRepository) {
            return other.id == id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}