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

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * A stack of Teams. This can be used a record of points scored.
 */
class ScoreStack : List<Team>, Parcelable {
    private val bitSet: BitSet
    override var size: Int
        private set

    constructor() {
        bitSet = BitSet()
        size = 0
    }

    constructor(bitSet: BitSet) {
        this.bitSet = bitSet
        this.size = bitSet.length()
    }

    private constructor(parcel: Parcel) {
        bitSet = parcel.readSerializable() as BitSet
        size = bitSet.length()
    }

    // TODO: Redundant?
    private inner class Itr : Iterator<Team> {
        internal var cursor = 0

        override fun hasNext() = cursor < size

        override fun next(): Team {
            val player = get(cursor)
            ++cursor
            return player
        }
    }

    private inner class ListItr(var cursor: Int = 0) : ListIterator<Team> {
        override fun hasNext() = cursor < size

        override fun hasPrevious() = cursor != 0 && size != 0

        override fun next(): Team {
            val player = get(cursor)
            ++cursor
            return player
        }

        override fun nextIndex() = cursor

        override fun previous(): Team {
            --cursor
            return get(cursor)
        }

        override fun previousIndex() = cursor - 1

    }

    private fun boolToTeam(teamBool: Boolean) = if (teamBool) Team.TEAM2 else Team.TEAM1
    private fun teamToBool(team: Team) = team != Team.TEAM1

    override fun contains(element: Team): Boolean {
        val bool = teamToBool(element)
        for (i in 0 until size) {
            if (bool == bitSet[i]) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Team>): Boolean {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int): Team {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException()
        }
        return boolToTeam(bitSet[index])
    }

    override fun indexOf(element: Team): Int {
        val bool = teamToBool(element)
        for (i in 0 until size) {
            if (bool == bitSet[i]) {
                return i
            }
        }
        return -1
    }

    override fun isEmpty() = size == 0

    override fun iterator(): Iterator<Team> {
        return Itr()
    }

    override fun lastIndexOf(element: Team): Int {
        val bool = teamToBool(element)
        for (i in size - 1 downTo 0) {
            if (bool == bitSet[i]) {
                return i
            }
        }
        return -1
    }

    override fun listIterator(): ListIterator<Team> {
        return ListItr()
    }

    override fun listIterator(index: Int): ListIterator<Team> {
        return ListItr(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Team> {
        if (fromIndex < 0)
            throw IndexOutOfBoundsException("fromIndex = $fromIndex")
        if (toIndex > size)
            throw IndexOutOfBoundsException("toIndex = $toIndex")
        if (fromIndex > toIndex)
            throw IllegalArgumentException("fromIndex($fromIndex) > toIndex($toIndex)")

        return ScoreStack(bitSet.get(fromIndex, toIndex))
    }

    /**
     * Pushed a Team to the top of the stack.
     *
     * @param element The Team to push.
     */
    fun push(element: Team) {
        bitSet[size] = teamToBool(element)
        ++size
    }

    /**
     * Pops the Team at the top of the stack.
     *
     * @return The popped Team.
     */
    fun pop() {
        if (size > 0) {
            --size
            bitSet.clear(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(bitSet)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ScoreStack> {
        override fun createFromParcel(parcel: Parcel): ScoreStack {
            return ScoreStack(parcel)
        }

        override fun newArray(size: Int): Array<ScoreStack?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Returns the underlying bitset representing the Team list converted to a Long array.
     */
    fun toLongArray(): LongArray = bitSet.toLongArray()
}
