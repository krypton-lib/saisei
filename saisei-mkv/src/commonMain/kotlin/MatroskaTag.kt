package saisei.container.mkv

import naibu.io.memory.Memory
import naibu.text.charset.decodeIntoString
import saisei.container.mkv.MatroskaTag.Target.Companion.readTagTarget
import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.mustBe
import kotlin.jvm.JvmInline

sealed class MatroskaTag {
    abstract val name: String

    abstract val target: Target

    val isGlobal: Boolean get() = target == Target.None

    val isAnyTrack: Boolean get() = !isGlobal && target.uid == 0L

    val isAnyEdition: Boolean get() = !isGlobal && target.uid == 0L

    val isAnyChapter: Boolean get() = !isGlobal && target.uid == 0L

    val isAnyAttachment: Boolean get() = !isGlobal && target.uid == 0L

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

        companion object {
            suspend fun MasterElement.readTagTarget(): Target? {
                this mustBe Segment.Tags.Tag.Targets

                return childOrNull(Segment.Tags.Tag.Targets.TagTrackUID)?.read()?.let(::Track)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagEditionUID)?.read()?.let(::Edition)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagChapterUID)?.read()?.let(::Chapter)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagAttachmentUID)?.read()?.let(::Attachment)
            }
        }
    }

    companion object {
        suspend fun MasterElement.readTags(): List<MatroskaTag> {
            this mustBe Segment.Tags
            return consumeFully()
                .children(Segment.Tags.Tag)
                .flatMap { it.readTag() }
        }

        private suspend fun MasterElement.readTag(): List<MatroskaTag> {
            this mustBe Segment.Tags.Tag

            val target = childOrNull(Segment.Tags.Tag.Targets)
                ?.readTagTarget()
                ?: Target.None

            return children(Segment.Tags.Tag.SimpleTag).map { it.readSimpleTag(target) }
        }

        private suspend fun MasterElement.readSimpleTag(target: Target): MatroskaTag {
            this mustBe Segment.Tags.Tag.SimpleTag

            val name = child(Segment.Tags.Tag.SimpleTag.TagName).read()
            return childOrNull(Segment.Tags.Tag.SimpleTag.TagString)?.let { STRING(name, target, it.read()) }
                ?: childOrNull(Segment.Tags.Tag.SimpleTag.TagBinary)?.let { BINARY(name, target, it.read()) }
                ?: error("SimpleTag is missing value element (TagString, TagBinary)")
        }
    }
}
