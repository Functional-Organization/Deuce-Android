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

class MatchList : MutableList<DeuceMatch> {
    companion object {
        val NON_CURRENT_MATCH = DeuceMatch()
        init {
            NON_CURRENT_MATCH.winner = Winner.TEAM1
        }
    }

    private val data: ArrayList<DeuceMatch>
    val file: File
    val backupFile: File
    private var writerThread: Thread? = null
    private var readerThread: Thread? = null
    private var currentMatch: DeuceMatch = NON_CURRENT_MATCH

    constructor(
        file: File,
        backupFile: File,
        partialLoadThreshold: Int,
        partialLoadSize: Int,
        partialLoaderCallback: () -> Unit
    ) {
        data = ArrayList()
        this.file = file
        this.backupFile = backupFile

        currentMatch.winner = Winner.TEAM1

        fun loadFile(file: File) {
            val parser = JSONParser()
            val reader = file.bufferedReader()
            val size = reader.readLine().toInt()
            val list = ArrayList<DeuceMatch>(size)

            fun readRest() {
                var line = reader.readLine()
                while (line != null) {
                    list.add(jsonObjectToMatch(parser.parse(line) as JSONObject))
                    line = reader.readLine()
                }
            }

            if (size > partialLoadThreshold) {
                for (i in 0 until minOf(partialLoadSize, size)) {
                    list.add(jsonObjectToMatch(parser.parse(reader.readLine()) as JSONObject))
                }
                data.addAll(list.asReversed())
                readerThread = thread {
                    try {
                        readRest()
                        data.clear()
                        data.addAll(list.asReversed())
                        partialLoaderCallback()
                    } catch (e: Exception) {
                        Log.d("foo", "Error loading the rest of score list file $file: $e")
                    }
                }
            } else {
                readRest()
                data.addAll(list.asReversed())
            }
        }

        if (file.exists()) {
            try {
                loadFile(file)
            } catch (e: Exception) {
                Log.d("foo", "Error loading scoreList file: $e")
                data.clear()
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

    constructor(file: File, backupFile: File, list: List<DeuceMatch>) {
        data = ArrayList(list)
        this.file = file
        this.backupFile = backupFile
    }

    private fun jsonObjectToMatch(json: JSONObject): DeuceMatch {
        return DeuceMatch(
            NumSets.fromOrdinal((json[KEY_NUM_SETS] as Long).toInt()),
            Team.fromOrdinal((json[KEY_SERVER] as Long).toInt()),
            OvertimeRule.fromOrdinal((json[KEY_OVERTIME_RULE] as Long).toInt()),
            MatchType.fromOrdinal((json[KEY_MATCH_TYPE] as Long).toInt()),
            PlayTimesData(json[KEY_MATCH_START_TIME] as Long, json[KEY_MATCH_END_TIME] as Long),
            PlayTimesList(
                ArrayList((json[KEY_SETS_START_TIMES] as JSONArray).map { it as Long }),
                ArrayList((json[KEY_SETS_END_TIMES] as JSONArray).map { it as Long })
            ),
            ScoreStack(
                (json[KEY_SCORE_SIZE] as Long).toInt(),
                BitSet.valueOf((json[KEY_SCORE_ARRAY] as JSONArray).map { it as Long }.toLongArray())
            ),
            json[KEY_NAME_TEAM1] as String,
            json[KEY_NAME_TEAM2] as String
        )
    }

    private fun matchToJSONObject(match: DeuceMatch): JSONObject {
        val json = JSONObject()

        json[KEY_NUM_SETS] = match.numSets.ordinal
        json[KEY_SERVER] = match.startingServer.ordinal
        json[KEY_OVERTIME_RULE] = match.overtimeRule.ordinal
        json[KEY_MATCH_TYPE] = match.matchType.ordinal
        json[KEY_MATCH_START_TIME] = match.playTimes.startTime
        json[KEY_MATCH_END_TIME] = match.playTimes.endTime
        val setsStartTimesJSON = JSONArray()
        setsStartTimesJSON.addAll(match.setsTimesLog.getStartTimesArray().toList())
        json[KEY_SETS_START_TIMES] = setsStartTimesJSON
        val setsEndTimesJSON = JSONArray()
        setsEndTimesJSON.addAll(match.setsTimesLog.getEndTimesArray().toList())
        json[KEY_SETS_END_TIMES] = setsEndTimesJSON
        json[KEY_SCORE_SIZE] = match.scoreLog.size
        val scoreLogArrayJSON = JSONArray()
        scoreLogArrayJSON.addAll(match.scoreLog.bitSetToLongArray().toList())
        json[KEY_SCORE_ARRAY] = scoreLogArrayJSON
        json[KEY_NAME_TEAM1] = match.nameTeam1
        json[KEY_NAME_TEAM2] = match.nameTeam2

        return json
    }

    fun writeToFile() {
        val matchJSONObjectList = data.map { matchToJSONObject(it) }
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

    override val size get() = if (currentMatch.winner == Winner.NONE) data.size + 1 else data.size

    override fun contains(element: DeuceMatch) = data.contains(element)

    override fun containsAll(elements: Collection<DeuceMatch>) = data.containsAll(elements)

    override fun get(index: Int) =
        if (index == lastIndex && currentMatch.winner == Winner.NONE)
            currentMatch
        else
            data[index]

    override fun indexOf(element: DeuceMatch) = data.lastIndexOf(element)

    override fun isEmpty() = data.isEmpty()

    override fun iterator(): MutableIterator<DeuceMatch> {
        readerThread?.join()
        return data.iterator()
    }

    override fun lastIndexOf(element: DeuceMatch) = data.lastIndexOf(element)

    override fun add(element: DeuceMatch): Boolean {
        if (element.winner == Winner.NONE) {
            currentMatch = element
            return true
        }
        readerThread?.join()
        return data.add(element)
    }

    override fun add(index: Int, element: DeuceMatch) {
        if (element.winner == Winner.NONE) {
            currentMatch = element
        } else {
            readerThread?.join()
            data.add(index, element)
        }
    }

    override fun addAll(index: Int, elements: Collection<DeuceMatch>): Boolean {
        readerThread?.join()
        return data.addAll(index, elements)
    }

    override fun addAll(elements: Collection<DeuceMatch>): Boolean {
        readerThread?.join()
        return data.addAll(elements)
    }

    override fun clear() {
        currentMatch = NON_CURRENT_MATCH
        readerThread?.join()
        data.clear()
    }

    override fun listIterator(): MutableListIterator<DeuceMatch> {
        readerThread?.join()
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<DeuceMatch> {
        readerThread?.join()
        return data.listIterator(index)
    }

    override fun remove(element: DeuceMatch): Boolean {
        if (element == currentMatch) {
            currentMatch = NON_CURRENT_MATCH
            return true
        }
        readerThread?.join()
        return data.remove(element)
    }

    override fun removeAll(elements: Collection<DeuceMatch>): Boolean {
        var tf = false
        readerThread?.join()
        for (element in elements) {
            if (element == currentMatch) {
                currentMatch = NON_CURRENT_MATCH
                tf = true
                break
            }
        }
        return data.removeAll(elements) || tf
    }

    override fun removeAt(index: Int): DeuceMatch {
        if (index == lastIndex) {
            val match = currentMatch
            currentMatch = DeuceMatch()
            currentMatch.winner = Winner.TEAM1
            return match
        }
        readerThread?.join()
        return data.removeAt(index)
    }

    override fun retainAll(elements: Collection<DeuceMatch>): Boolean {
        var removeCurrentMatch = true
        for (element in elements) {
            if (element == currentMatch)
            removeCurrentMatch = false
            break
        }
        if (removeCurrentMatch) {
            currentMatch = NON_CURRENT_MATCH
        }
        readerThread?.join()
        return data.retainAll(elements) || removeCurrentMatch
    }

    override fun set(index: Int, element: DeuceMatch): DeuceMatch {
        if (index == lastIndex) {
            val match = currentMatch
            currentMatch = element
            return match
        }
        readerThread?.join()
        return data.set(index, element)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<DeuceMatch> {
        TODO("Not yet implemented")
    }
}
