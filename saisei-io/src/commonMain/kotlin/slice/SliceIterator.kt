package saisei.io.slice

import kotlinx.atomicfu.atomic

public data class SliceIterator<T>(public val slice: Slice<T>) : Iterator<T> {
    private var position by atomic(0L)

    override fun hasNext(): Boolean = position in slice.indices

    override fun next(): T = try {
        slice[position++]
    } catch (ex: IndexOutOfBoundsException) {
        position--
        throw NoSuchElementException(ex.message)
    }

    override fun toString(): String = "SliceIterator(slice=$slice, position=$position)"
}
