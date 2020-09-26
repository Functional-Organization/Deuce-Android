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

package net.mqduck.deuce

import net.mqduck.deuce.common.DeuceMatch
import net.mqduck.deuce.common.MatchList
import net.mqduck.deuce.common.Winner
import java.io.File

class MobileMatchList(
    file: File,
    backupFile: File,
    partialLoadThreshold: Int,
    partialLoadSize: Int,
    partialLoaderCallback: () -> Unit
) : MatchList(file, backupFile, partialLoadThreshold, partialLoadSize, partialLoaderCallback) {
    inner class MobileMatchListIterator(private var cursor: Int = 0) : MutableListIterator<DeuceMatch> {
        override fun hasNext() = cursor < size

        override fun hasPrevious() = cursor != 0

        override fun next(): DeuceMatch {
            val match = get(cursor)
            ++cursor
            return match
        }

        override fun nextIndex() = cursor

        override fun previous(): DeuceMatch {
            --cursor
            return get(cursor)
        }

        override fun previousIndex() = cursor - 1

        override fun add(element: DeuceMatch) = this@MobileMatchList.add(cursor, element)

        override fun remove() {
            this@MobileMatchList.removeAt(cursor)
        }

        override fun set(element: DeuceMatch) {
            this@MobileMatchList[cursor] = element
        }
    }

    companion object {
        private val NON_CURRENT_MATCH = DeuceMatch()

        init {
            NON_CURRENT_MATCH.winner =
                Winner.TEAM1
        }
    }

    private var currentMatch = NON_CURRENT_MATCH

    private val hasCurrentMatch get() = currentMatch.isOngoing

    override val size get() = if (hasCurrentMatch) data.size + 1 else data.size

    override fun contains(element: DeuceMatch) =
        data.contains(element) || (hasCurrentMatch && element == currentMatch)

    override fun containsAll(elements: Collection<DeuceMatch>): Boolean {
        if (hasCurrentMatch) {
            for (element in elements) {
                if (!data.contains(element) && element != currentMatch) {
                    return false
                }
            }
            return true
        } else {
            for (element in elements) {
                if (!data.contains(element)) {
                    return false
                }
            }
            return true
        }
    }

    override fun get(index: Int) =
        if (index == lastIndex && hasCurrentMatch)
            currentMatch
        else
            data[index]

    override fun indexOf(element: DeuceMatch): Int {
        val index = data.indexOf(element)
        if (index == -1 && hasCurrentMatch && element == currentMatch) {
            return data.size
        }
        return index
    }

    override fun isEmpty() = data.isEmpty()

    override fun iterator() = MobileMatchListIterator()

    override fun lastIndexOf(element: DeuceMatch): Int {
        if (hasCurrentMatch && element == currentMatch) {
            return data.size
        }
        return data.lastIndexOf(element)
    }

    override fun listIterator() = MobileMatchListIterator()

    override fun listIterator(index: Int) = MobileMatchListIterator(index)

    override fun add(element: DeuceMatch): Boolean {
        if (element.isOngoing) {
            currentMatch = element
            return true
        }
        readerThread?.join()
        return data.add(element)
    }

    override fun add(index: Int, element: DeuceMatch) {
        if (element.isOngoing) {
            currentMatch = if (hasCurrentMatch) {
                if (index == lastIndex)
                    element
                else
                    throw IllegalArgumentException("An unfinished match is only allowed at the end of the list.")
            } else if (index == data.size) {
                element
            } else {
                throw IllegalArgumentException("An unfinished match is only allowed at the end of the list.")
            }
        } else {
            readerThread?.join()
            data.add(index, element)
        }
    }

    // TODO: Check for ongoing matches
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
            currentMatch = NON_CURRENT_MATCH
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
