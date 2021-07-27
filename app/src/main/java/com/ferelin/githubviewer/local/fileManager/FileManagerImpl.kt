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

package com.ferelin.githubviewer.local.fileManager

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import okio.IOException
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManagerImpl @Inject constructor(
    private val mContext: Context
) : FileManager {

    override fun cacheZipToSelectedPath(
        inputStreamData: InputStream,
        treePath: String,
        pathAuthority: String,
        fileName: String
    ): String? {
        var outputStream: FileOutputStream? = null

        return try {
            val newZipFile = createFileByPath(fileName, treePath, pathAuthority)
            val writeMode = "w"

            mContext.contentResolver.openFileDescriptor(newZipFile!!.uri, writeMode)!!
                .use { parcelFileDescriptor ->
                    outputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
                        .also { outStream ->
                            val writeBuffer = ByteArray(1024)

                            var length: Int
                            while ((inputStreamData.read(writeBuffer)).also { length = it } >= 0) {
                                outStream.write(writeBuffer, 0, length)
                            }
                            outStream.flush()
                            outStream.close()
                            inputStreamData.close()
                        }
                }
            buildFilePath(newZipFile)
        } catch (unsupportedUri: UnsupportedOperationException) {
            // Do nothing
            null
        } catch (nullPointer: NullPointerException) {
            // Do nothing
            null
        } catch (ioException: IOException) {
            // Do nothing
            null
        } finally {
            outputStream?.close()
            inputStreamData.close()
        }
    }

    /**
     * Builds uri by path and authority -> creates document by uri -> creates new file
     * */
    private fun createFileByPath(
        fileName: String,
        treePath: String,
        pathAuthority: String
    ): DocumentFile? {
        val uriByPath = Uri.Builder()
            .path(treePath)
            .authority(pathAuthority)
            .build()

        val documentByUri = DocumentFile.fromTreeUri(mContext, uriByPath)
        val resultFileName = "/$fileName.zip"
        return documentByUri!!.createFile("*/zip", resultFileName)
    }

    /**
     * Builds file path from source [DocumentFile].
     * */
    private fun buildFilePath(file: DocumentFile): String {
        var finalPath = "${file.name}"
        var parentFile = file.parentFile

        while (parentFile != null) {
            finalPath = "${parentFile.name}/$finalPath"
            parentFile = parentFile.parentFile
        }

        return "sdcard/$finalPath"
    }
}