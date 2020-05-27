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
import java.io.File

class WearMatchList(
    file: File,
    backupFile: File,
    partialLoadThreshold: Int,
    partialLoadSize: Int,
    partialLoaderCallback: () -> Unit
) : MatchList(file, backupFile, partialLoadThreshold, partialLoadSize, partialLoaderCallback) {
    override val size get() = data.size
    override fun contains(element: DeuceMatch) = data.contains(element)
    override fun containsAll(elements: Collection<DeuceMatch>) = data.containsAll(elements)
    override fun get(index: Int) = data[index]
    override fun indexOf(element: DeuceMatch) = data.indexOf(element)
    override fun isEmpty() = data.isEmpty()
    override fun iterator() = data.iterator()
    override fun lastIndexOf(element: DeuceMatch) = data.lastIndexOf(element)
    override fun listIterator() = data.listIterator()
    override fun listIterator(index: Int) = data.listIterator(index)
    override fun add(element: DeuceMatch) = data.add(element)
    override fun add(index: Int, element: DeuceMatch) = data.add(index, element)
    override fun addAll(index: Int, elements: Collection<DeuceMatch>) = data.addAll(index, elements)
    override fun addAll(elements: Collection<DeuceMatch>) = data.addAll(elements)
    override fun clear() = data.clear()
    override fun remove(element: DeuceMatch) = data.remove(element)
    override fun removeAll(elements: Collection<DeuceMatch>) = data.removeAll(elements)
    override fun removeAt(index: Int) = data.removeAt(index)
    override fun retainAll(elements: Collection<DeuceMatch>) = data.retainAll(elements)
    override fun set(index: Int, element: DeuceMatch) = data.set(index, element)
    override fun subList(fromIndex: Int, toIndex: Int) = data.subList(fromIndex, toIndex)
}
