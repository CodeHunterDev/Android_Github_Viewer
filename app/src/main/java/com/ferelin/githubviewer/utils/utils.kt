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

import android.content.ActivityNotFoundException
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ferelin.githubviewer.R
import java.util.*
import kotlin.concurrent.timerTask

fun withTimer(time: Long = 200L, body: () -> Unit) {
    Timer().schedule(timerTask {
        body.invoke()
    }, time)
}

fun Animation.invalidate() {
    setAnimationListener(null)
    cancel()
}

fun avoidNotFoundException(context: Context, body: () -> Unit) {
    try {
        body.invoke()
    } catch (exception: ActivityNotFoundException) {
        showError(context, context.getString(R.string.notificationNotFoundActivity))
    }
}

fun showError(context: Context, errorMessage: String) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}