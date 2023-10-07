package saisei.io.format.ebml.element

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import saisei.io.exception.EOFException

public interface MasterElement : Element {
    /**
     * Whether this Master element is empty (hasn't been read yet).
     */
    public val isEmpty: Boolean

    /**
     * The elements that have been read to this master element.
     */
    public val children: List<Element>

    @ExperimentalSaiseiApi
    public fun reader(): MasterElementReader

    public suspend fun <T> consume(block: suspend MasterElementReader.() -> T): T

    public data class Actual(override val header: ElementHeader, override val children: List<Element>) : MasterElement {
        override val isEmpty: Boolean
            get() = false

        @ExperimentalSaiseiApi
        override fun reader(): MasterElementReader = object : MasterElementReader {
            private var i = 0
            private val mutex = Mutex()

            override val header: ElementHeader
                get() = this@Actual.header

            override val remaining: Long
                get() = if (i > children.size) 0 else children.drop(i).fold(0) { acc, s -> acc + s.header.size }

            override val closed: Boolean
                get() = false

            override val children: List<Element>
                get() = this@Actual.children

            override suspend fun readNextChild(): Element = mutex.withLock {
                children.elementAtOrNull(i++) ?: throw EOFException("No more children")
            }

            override suspend fun skipLastChild() {
            }

            override fun close() {
            }

            override fun self(): MasterElement = this@Actual
        }

        @OptIn(ExperimentalSaiseiApi::class)
        override suspend fun <T> consume(block: suspend MasterElementReader.() -> T): T =
            reader().block()

        override fun toString(): String = "Element::Master(header=$header, children=$children)"
    }
}
