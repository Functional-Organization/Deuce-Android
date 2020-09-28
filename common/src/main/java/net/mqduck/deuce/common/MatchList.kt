/*
 * Copyright (C) 2020 Jeffrey Thomas Piercy
 *
 * This file is part of Deuce-Android.
 *
 * Deuce-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deuce-Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Deuce-Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mqduck.deuce.common

import android.util.Log
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

abstract class MatchList (
    private val file: File,
    private val backupFile: File,
    partialLoadThreshold: Int,
    partialLoadSize: Int,
    partialLoaderCallback: () -> Unit
) : MutableList<DeuceMatch> {
    protected var data = ArrayList<DeuceMatch>()
    private var writerThread: Thread? = null
    protected var readerThread: Thread? = null

    init {
        // TODO: Doesn't load backup save file if main save file is corrupt past partialLoadThreshold
        fun loadFile(file: File) {
            val reader = file.bufferedReader()
            val size = reader.readLine().toInt()
            val list = ArrayList<DeuceMatch>(size)
            val parser = JSONParser()

            fun readRest() {
                var line = reader.readLine()
                while (line != null) {
                    list.add(DeuceMatch(parser.parse(line) as JSONObject))
                    line = reader.readLine()
                }
            }

            if (size > partialLoadThreshold) {
                for (i in 0 until minOf(partialLoadSize, size)) {
                    list.add(DeuceMatch(parser.parse(reader.readLine()) as JSONObject))
                }
                data = ArrayList(list.asReversed())
                readerThread = thread {
                    try {
                        readRest()
                        data = ArrayList(list.asReversed())
                        partialLoaderCallback()
                    } catch (e: Exception) {
                        Log.d("foo", "Error loading the rest of score list file $file: $e")
                    }
                }
            } else {
                readRest()
                //data.addAll(list.asReversed())
                data = ArrayList(list.asReversed())
            }
        }

        if (file.exists()) {
            try {
                loadFile(file)
            } catch (e: Exception) {
                Log.d("foo", "Error loading scoreList file: $e")
                if (backupFile.exists()) {
                    try {
                        loadFile(backupFile)
                        backupFile.copyTo(file, true)
                    } catch (f: java.lang.Exception) {
                        Log.d("foo", "Error loading or copying backup scoreList file: $f")
                    }
                }
            }
        }
    }

    fun clean() {
        readerThread?.join()
        data = ArrayList(data.toSet().sorted())
    }

    fun writeToFile() {
        val matchJSONObjectList = data.map { it.toJSONObject() }
        readerThread?.join()
        writerThread?.join()
        writerThread = thread {
            val fileWriter = file.bufferedWriter()
            fileWriter.write("${matchJSONObjectList.size}\n")
            matchJSONObjectList.asReversed().forEach { fileWriter.write("${it}\n") }
            fileWriter.flush()
            fileWriter.close()
            file.copyTo(backupFile, true)
        }
    }
}
