package saisei.io.format.ebml

import naibu.io.memory.Memory
import naibu.io.slice.Slice
import naibu.io.slice.asSlice

sealed interface EbmlID {
    val code: Long
    val name: String

    /**
     *
     */
    class Coded(override val code: Long, override val name: String = "$code") : EbmlID {
        override fun toString(): String = "$code"
    }

    /**
     *
     */
    class Binary(private val data: Slice, override val name: String) : EbmlID {
        constructor(data: Memory, name: String) : this(data.asSlice(), name)

        constructor(data: ByteArray, name: String) : this(data.asSlice(), name)

        override val code: Long get() = data.readEBMLInteger()

        override fun toString(): String = if (name.isBlank()) "$code" else "$code ($name)" // "EbmlID(code=$code, name=$name)"
    }

    infix fun notMatches(other: EbmlID): Boolean {
        return other.code != code
    }

    infix fun matches(other: EbmlID): Boolean {
        return other.code == code
    }

    companion object {
        operator fun invoke(name: String, vararg numbers: Number): Binary =
            Binary(ByteArray(numbers.size) { numbers[it].toByte() }, name)
    }
}
