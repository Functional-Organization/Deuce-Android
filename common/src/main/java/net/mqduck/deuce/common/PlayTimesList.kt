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

class PlayTimesList : MutableList<PlayTimes> {
    private inner class ListItr(private var cursor: Int) : MutableListIterator<PlayTimes> {
        override fun hasPrevious() = cursor != 0

        override fun nextIndex() = cursor

        override fun previous(): PlayTimes {
            --cursor
            return get(cursor)
        }

        override fun previousIndex() = cursor - 1

        override fun add(element: PlayTimes) {
            startTimes.add(cursor, element.startTime)
            endTimes.add(cursor, element.endTime)
        }

        override fun hasNext() = cursor < size

        override fun next(): PlayTimes {
            val playTimes = get(cursor)
            ++cursor
            return playTimes
        }

        override fun remove() {
            startTimes.removeAt(cursor)
            endTimes.removeAt(cursor)
        }

        override fun set(element: PlayTimes) {
            startTimes[cursor] = element.startTime
            endTimes[cursor] = element.endTime
        }
    }

    var startTimes: ArrayList<Long> = ArrayList()
    var endTimes: ArrayList<Long> = ArrayList()

    constructor() {
        startTimes = ArrayList()
        endTimes = ArrayList()
    }

    constructor(startTimes: LongArray, endTimes: LongArray) {
        this.startTimes = startTimes.toCollection(ArrayList())
        this.endTimes = endTimes.toCollection(ArrayList())
    }

    constructor(startTimes: ArrayList<Long>, endTimes: ArrayList<Long>) {
        this.startTimes = startTimes
        this.endTimes = endTimes
    }

    fun getStartTimesArray() = startTimes.toLongArray()

    fun getEndTimesArray() = endTimes.toLongArray()

    override val size get() = startTimes.size

    override fun contains(element: PlayTimes) = indexOf(element) >= 0

    override fun containsAll(elements: Collection<PlayTimes>): Boolean {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int) = object : PlayTimes {
        override var startTime: Long
            get() = startTimes[index]
            set(value) {
                startTimes[index] = value
            }
        override var endTime: Long
            get() = endTimes[index]
            set(value) {
                endTimes[index] = value
            }
    }

    override fun indexOf(element: PlayTimes): Int {
        for (i in 0 until size) {
            if (startTimes[i] == element.startTime && endTimes[i] == element.endTime) {
                return i
            }
        }
        return -1
    }

    override fun isEmpty() = startTimes.isEmpty()

    override fun iterator(): MutableIterator<PlayTimes> = ListItr(0)

    override fun lastIndexOf(element: PlayTimes): Int {
        for (i in size - 1 downTo 0) {
            if (startTimes[i] == element.startTime && endTimes[i] == element.endTime) {
                return i
            }
        }
        return -1
    }

    override fun add(element: PlayTimes): Boolean {
        startTimes.add(element.startTime)
        endTimes.add(element.endTime)
        return true
    }

    override fun add(index: Int, element: PlayTimes) {
        startTimes.add(index, element.startTime)
        endTimes.add(index, element.endTime)
    }

    override fun addAll(index: Int, elements: Collection<PlayTimes>): Boolean {
        startTimes.addAll(index, elements.map { it.startTime })
        return endTimes.addAll(index, elements.map { it.endTime })
    }

    override fun addAll(elements: Collection<PlayTimes>): Boolean {
        for (element in elements) {
            add(element)
        }
        return elements.isNotEmpty()
    }

    override fun clear() {
        startTimes.clear()
        endTimes.clear()
    }

    override fun listIterator(): MutableListIterator<PlayTimes> = ListItr(0)

    override fun listIterator(index: Int): MutableListIterator<PlayTimes> = ListItr(index)

    override fun remove(element: PlayTimes): Boolean {
        val index = indexOf(element)
        if (index >= 0) {
            startTimes.removeAt(index)
            endTimes.removeAt(index)
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<PlayTimes>) = batchRemove(elements, false)

    override fun removeAt(index: Int): PlayTimes {
        val removedElement = PlayTimesData(startTimes[index], endTimes[index])
        startTimes.removeAt(index)
        endTimes.removeAt(index)
        return removedElement
    }

    override fun retainAll(elements: Collection<PlayTimes>) = batchRemove(elements, true)

    private fun batchRemove(elements: Collection<PlayTimes>, complement: Boolean): Boolean {
        var r = 0
        var w = 0
        while (r < size) {
            if (elements.contains(get(r)) == complement) {
                startTimes[w] = startTimes[r]
                endTimes[w] = endTimes[r]
                ++w
            }
            ++r
        }

        if (w != size) {
            startTimes = ArrayList(startTimes.subList(0, w))
            endTimes = ArrayList(endTimes.subList(0, w))
            return true
        }
        return false
    }

    override fun set(index: Int, element: PlayTimes): PlayTimes {
        val previous = PlayTimesData(startTimes[index], endTimes[index])
        startTimes[index] = element.startTime
        endTimes[index] = element.endTime
        return previous
    }

    // TODO: Optimize? Probably not.
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<PlayTimes> = PlayTimesList(
        ArrayList(startTimes.subList(fromIndex, toIndex)),
        ArrayList(endTimes.subList(fromIndex, toIndex))
    )
}
