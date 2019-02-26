/*
 * Copyright (C) 2019 Jeffrey Thomas Piercy
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

package org.subhipstercollective.deucelibrary

import java.util.*

class ScoreStack : List<Player> {
    private val bitSet = BitSet()

    private inner class Itr : Iterator<Player> {
        internal var cursor = 0

        override fun hasNext() = cursor < size

        override fun next(): Player {
            val player = get(cursor)
            ++cursor
            return player
        }
    }

    private inner class ListItr(internal var cursor: Int = 0) : ListIterator<Player> {
        override fun hasNext() = cursor < size

        override fun hasPrevious() = cursor != 0

        override fun next(): Player {
            val player = get(cursor)
            ++cursor
            return player
        }

        override fun nextIndex() = cursor

        override fun previous(): Player {
            --cursor
            return get(cursor)
        }

        override fun previousIndex() = cursor - 1

    }

    private fun boolToPlayer(playerInt: Boolean) = if (playerInt) Player.PLAYER2 else Player.PLAYER1
    private fun playerToBool(player: Player) = player != Player.PLAYER1

    override var size = 0
        private set

    override fun contains(element: Player): Boolean {
        val bool = playerToBool(element)
        for (i in 0 until size) {
            if (bool == bitSet[i]) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Player>): Boolean {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int) = boolToPlayer(bitSet[index])

    override fun indexOf(element: Player): Int {
        val bool = playerToBool(element)
        for (i in 0 until size) {
            if (bool == bitSet[i]) {
                return i
            }
        }
        return -1
    }

    override fun isEmpty(): Boolean {
        return bitSet.isEmpty
    }

    override fun iterator(): Iterator<Player> {
        return Itr()
    }

    override fun lastIndexOf(element: Player): Int {
        val bool = playerToBool(element)
        for (i in size - 1 downTo 0) {
            if (bool == bitSet[i]) {
                return i
            }
        }
        return -1
    }

    override fun listIterator(): ListIterator<Player> {
        return ListItr()
    }

    override fun listIterator(index: Int): ListIterator<Player> {
        return ListItr(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Player> {
        //TODO: do
        return ScoreStack()
    }

    fun push(element: Player) {
        bitSet[size] = playerToBool(element)
        ++size
    }

    fun pop() {
        if (size > 0) {
            --size
            bitSet.clear(size)
        }
    }
}
