package saisei.io.format.ebml.element

import saisei.io.memory.ByteMemory

public interface BinaryElement : Element {
    public suspend fun read(): ByteMemory

    public data class Lazy(
        override val header: ElementHeader,
        val load: suspend BinaryElement.() -> ByteMemory,
    ) : BinaryElement {
        override suspend fun read(): ByteMemory = load()
    }

    public companion object {
        /**
         * Creates a new [BinaryElement] with the given [data].
         */
        public inline fun of(header: ElementHeader, data: ByteMemory): BinaryElement =
            Lazy(header) { data }
    }
}
