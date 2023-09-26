package saisei.io.format.ebml.element

import naibu.io.memory.Memory

interface BinaryElement : Element {
    suspend fun read(): Memory

    data class Lazy(
        override val header: ElementHeader,
        val load: suspend BinaryElement.() -> Memory,
    ) : BinaryElement {
        override suspend fun read(): Memory = load()
    }

    companion object {
        /**
         * Creates a new [BinaryElement] with the given [data].
         */
        inline fun of(header: ElementHeader, data: Memory) = Lazy(header) {
            data
        }
    }
}
