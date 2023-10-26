package saisei.container.mkv

import saisei.container.mkv.MatroskaTag.Target.Companion.readTagTarget
import saisei.container.mkv.element.Segment
import saisei.io.charset.decodeIntoString
import saisei.io.format.ebml.element.*
import saisei.io.format.ebml.mustBe
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.asSlice
import kotlin.jvm.JvmInline

public sealed class MatroskaTag {
    public abstract val name: String

    public abstract val target: Target

    public val isGlobal: Boolean get() = target == Target.None

    public val isAnyTrack: Boolean get() = !isGlobal && target.uid == 0L

    public val isAnyEdition: Boolean get() = !isGlobal && target.uid == 0L

    public val isAnyChapter: Boolean get() = !isGlobal && target.uid == 0L

    public val isAnyAttachment: Boolean get() = !isGlobal && target.uid == 0L

    /**
     * Get the tag content as a [String].
     */
    public abstract fun asString(): String

    /**
     * Get the tag content as a [Memory] instance.
     */
    public abstract fun asBinary(): ByteSlice

    public data class STRING(
        override val name: String,
        override val target: Target,
        val value: String,
    ) : MatroskaTag() {
        override fun asString(): String = value
        override fun asBinary(): ByteSlice = value.encodeToByteArray().asSlice()
    }

    public data class BINARY(
        override val name: String,
        override val target: Target,
        val value: ByteMemory,
    ) : MatroskaTag() {
        override fun asString(): String = value.decodeIntoString()
        override fun asBinary(): ByteSlice = value.asSlice()
    }

    public sealed interface Target {
        public val uid: Long

        public data object None : Target {
            override val uid: Long = 0
        }

        @JvmInline
        public value class Attachment(override val uid: Long) : Target

        @JvmInline
        public value class Edition(override val uid: Long) : Target

        @JvmInline
        public value class Chapter(override val uid: Long) : Target

        @JvmInline
        public value class Track(override val uid: Long) : Target

        public companion object {
            public suspend fun MasterElement.readTagTarget(): Target? {
                this mustBe Segment.Tags.Tag.Targets

                return childOrNull(Segment.Tags.Tag.Targets.TagTrackUID)?.read()?.let(::Track)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagEditionUID)?.read()?.let(::Edition)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagChapterUID)?.read()?.let(::Chapter)
                    ?: childOrNull(Segment.Tags.Tag.Targets.TagAttachmentUID)?.read()?.let(::Attachment)
            }
        }
    }

    public companion object {
        public suspend fun MasterElement.readTags(): List<MatroskaTag> {
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
