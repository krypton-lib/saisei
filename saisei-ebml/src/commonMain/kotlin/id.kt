package saisei.io.format.ebml

import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.asSlice

public sealed interface EbmlID {
    public val code: Long
    public val name: String

    /**
     *
     */
    public class Coded(override val code: Long, override val name: String = "$code") : EbmlID {
        override fun toString(): String = "$code"
    }

    /**
     *
     */
    public class Binary(private val data: ByteSlice, override val name: String) : EbmlID {
        public constructor(data: ByteMemory, name: String) : this(data.asSlice(), name)

        public constructor(data: ByteArray, name: String) : this(data.asSlice(), name)

        override val code: Long get() = data.readEBMLInteger()

        override fun toString(): String = if (name.isBlank()) "$code" else "$code ($name)" // "EbmlID(code=$code, name=$name)"
    }

    public infix fun notMatches(other: EbmlID): Boolean {
        return other.code != code
    }

    public infix fun matches(other: EbmlID): Boolean {
        return other.code == code
    }

    public companion object {
        public operator fun invoke(name: String, vararg numbers: Number): Binary =
            Binary(ByteArray(numbers.size) { numbers[it].toByte() }, name)
    }
}
