package saisei.container.mkv

import naibu.io.memory.Memory
import naibu.text.charset.decodeIntoString
import kotlin.jvm.JvmInline

sealed class MatroskaTag {
    abstract val name: String

    abstract val target: Target

    /**
     * Get the tag content as a [String].
     */
    abstract fun asString(): String

    /**
     * Get the tag content as a [Memory] instance.
     */
    abstract fun asBinary(): Memory

    data class STRING(override val name: String, override val target: Target, val value: String) : MatroskaTag() {
        override fun asString(): String = value
        override fun asBinary(): Memory = Memory.of(value.encodeToByteArray())
    }

    data class BINARY(override val name: String, override val target: Target, val value: Memory) : MatroskaTag() {
        override fun asString(): String = value.decodeIntoString()
        override fun asBinary(): Memory = value
    }

    sealed interface Target {
        val uid: Long

        data object None : Target {
            override val uid: Long = 0
        }

        @JvmInline
        value class Attachment(override val uid: Long) : Target

        @JvmInline
        value class Edition(override val uid: Long) : Target

        @JvmInline
        value class Chapter(override val uid: Long) : Target

        @JvmInline
        value class Track(override val uid: Long) : Target
    }
}
