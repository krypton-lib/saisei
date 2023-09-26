package saisei.io.format.ebml.element

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Instant
import naibu.cio.stream.read.SeekableReadStream
import naibu.cio.stream.read.readDouble
import naibu.cio.stream.read.readFloat
import naibu.common.Closeable
import naibu.io.exception.EOFException
import naibu.io.memory.DefaultAllocator
import naibu.io.memory.Memory
import naibu.math.toIntSafe
import naibu.text.charset.Charsets
import naibu.text.charset.decodeIntoString
import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.readFixedSizeEBMLInteger
import saisei.io.format.ebml.readVariableEBMLInteger

data class MasterElementDeclarationReader(
    val declaration: ElementDeclaration.MASTER,
    override val header: ElementHeader,
    val stream: SeekableReadStream,
    val parent: MasterElementDeclarationReader? = null,
) : MasterElementReader, Closeable {
    internal val mutableClosed = atomic(false)
    internal val mutex = Mutex()
    internal val mutableChildren = mutableListOf<Element>()
    internal var position: Long = 0

    internal var lastElement: ElementHeader? = null
    internal var lastElementRead: Boolean = false

    override val remaining
        get() = header.dataSize - position

    override val closed: Boolean
        get() = mutableClosed.value

    override val children: List<Element>
        get() = mutableChildren.toList()

    override fun close() {
        mutableClosed.update { true }
    }

    private suspend fun readNextChildHeader(): Pair<ElementHeader, ElementDeclaration<Element>?> {
        /* read an element header. */
        val header = ElementHeader.read(stream)

        // find a child that matches the ID of the read header.
        val child = declaration.children.find { it.id matches header.id }
            ?: return header to null

        return header.copy(id = child.id) to child
    }

    override suspend fun readNextChild(): Element? {
        require(!mutableClosed.value) {
            "Reader is closed..."
        }

        mutex.withLock {
            if (!lastElementRead) lastElement?.let { last ->
                val remaining = last.calculateRemaining(stream.position)
                position += if (remaining > 0) {
                    /* if the last element was not read entirely then skip it. */
                    stream.discardFully(remaining)
                    remaining
                } else {
                    last.dataSize
                }
            }

            lastElement = null
            lastElementRead = false

            /*  */
            if (remaining <= 0) {
                close()
                throw EOFException("Used all available data")
            }

            /* read the header of the next child. */
            val (hdr, child) = readNextChildHeader()
            position += hdr.headerSize

            /* if the header returns an element that is unknown to this master element just skip it. */
            if (child == null) {
                stream.discardFully(hdr.dataSize)
                position += hdr.dataSize
                return null
            }

            // set the last element to the child that was just read.
            lastElement = hdr

            //
            val element = when (child) {
                is ElementDeclaration.CUSTOM<*> -> CUSTOM(hdr, child::process, this)
                is ElementDeclaration.INTEGER -> INT(hdr, child.type, this)
                is ElementDeclaration.MASTER -> MASTER(hdr, child, this)
                is ElementDeclaration.STRING -> STRING(hdr, child.unicode, this)
                is ElementDeclaration.BINARY -> BINARY(hdr, this)
                is ElementDeclaration.FLOAT -> FLOAT(hdr, this)
                is ElementDeclaration.DATE -> DATE(hdr, this)
            }

            mutableChildren += element
            return element
        }
    }

    abstract class BaseElement<T>(val reader: MasterElementDeclarationReader) {
        abstract val header: ElementHeader

        protected var value: T? = null

        protected abstract suspend fun readInner(): T

        protected suspend fun readValue(): T {
            // if the value is cached, return it.
            if (value != null) return value!!

            //
            reader.mutex.withLock {
                require(reader.lastElement == header) {
                    "Unable to read the data of this element, another child has been read."
                }

                val pos = reader.stream.position
                value = readInner()
                reader.position += reader.stream.position - pos
                reader.lastElementRead = true

                return requireNotNull(value) {
                    "Unable to read "
                }
            }
        }
    }

    class DATE(
        override val header: ElementHeader,
        reader: MasterElementDeclarationReader,
    ) : DateElement, BaseElement<Instant>(reader) {
        override suspend fun read(): Instant = readValue()

        override suspend fun readInner(): Instant {
            /* https://www.rfc-editor.org/rfc/rfc8794.pdf 7.6. Date Element */
            val data = reader.stream.readVariableEBMLInteger(EBMLIntegerType.Signed)

            // convert from nanoseconds -> seconds then add the offset.
            return Instant.fromEpochMilliseconds(data / 1_000_000_000 + UNIX_EPOCH_DELAY_SECONDS)
        }

        override fun toString(): String =
            "Element::Date(header=$header, ${value?.let { ", value=$it" } ?: ""})"

        companion object {
            /* 2001-01-01T00:00:00.000000000 UTC */
            private const val UNIX_EPOCH_DELAY_SECONDS = 978307200
        }
    }

    class CUSTOM<T>(
        override val header: ElementHeader,
        val processor: suspend (Memory) -> T,
        reader: MasterElementDeclarationReader,
    ) : CustomElement<T>, BaseElement<T>(reader) {
        override suspend fun read(): T = readValue()

        override suspend fun readInner(): T {
            val data = DefaultAllocator.allocate(header.dataSize)
            reader.stream.readFully(data)

            return processor(data)
        }

        override fun toString(): String =
            "Element::Custom(header=$header${value?.let { ", value=$it" } ?: ""})"
    }

    class BINARY(
        override val header: ElementHeader,
        reader: MasterElementDeclarationReader,
    ) : BinaryElement, BaseElement<Memory>(reader) {
        override suspend fun read(): Memory = readValue()

        override suspend fun readInner(): Memory {
            val data = DefaultAllocator.allocate(header.dataSize)
            reader.stream.readFully(data)

            return data
        }
    }

    class INT(
        override val header: ElementHeader,
        override val type: EBMLIntegerType,
        reader: MasterElementDeclarationReader,
    ) : IntegerElement, BaseElement<Long>(reader) {
        override suspend fun read(): Long = readValue()

        override suspend fun readInner(): Long =
            reader.stream.readFixedSizeEBMLInteger(header.dataSize.toIntSafe(), type)


        override fun toString(): String =
            "Element::Int(header=$header, type=$type${value?.let { ", value=$it" } ?: ""})"
    }

    class FLOAT(
        override val header: ElementHeader,
        reader: MasterElementDeclarationReader,
    ) : FloatElement, BaseElement<Double>(reader) {
        override suspend fun read(): Double = readValue()

        override suspend fun readInner(): Double = when (header.dataSize) {
            4L -> reader.stream.readFloat().toDouble()
            8L -> reader.stream.readDouble()
            else -> error("Float element is too large: ${header.dataSize}")
        }

        override fun toString(): String = "Element::Float(header=$header${value?.let { ", value=$it" } ?: ""})"
    }

    class STRING(
        override val header: ElementHeader,
        override val unicode: Boolean,
        reader: MasterElementDeclarationReader,
    ) : StringElement, BaseElement<String>(reader) {
        override suspend fun read(): String = readValue()

        override suspend fun readInner(): String {
            val data = DefaultAllocator.allocate(header.dataSize)
            reader.stream.readFully(data)

            return data.decodeIntoString(charset = if (unicode) Charsets.UTF_8 else Charsets.US_ASCII)
        }

        override fun toString(): String =
            "Element::String(header=$header, unicode=$unicode${value?.let { ", value=$it" } ?: ""})"
    }

    @OptIn(ExperimentalSaiseiApi::class)
    class MASTER(
        override val header: ElementHeader,
        val declaration: ElementDeclaration.MASTER,
        val reader: MasterElementDeclarationReader,
    ) : MasterElement {
        private lateinit var childrenCache: List<Element>

        override val children: List<Element>
            get() = if (isEmpty) emptyList() else childrenCache

        override val isEmpty: Boolean
            get() = !::childrenCache.isInitialized

        override fun reader(): MasterElementReader {
            TODO("Not yet implemented")
        }

        override suspend fun <T> consume(block: suspend MasterElementReader.() -> T): T = reader.mutex.withLock {
            if (::childrenCache.isInitialized) {
                return MasterElement.Actual(header, childrenCache).consume(block)
            }

            val r = MasterElementDeclarationReader(declaration, header, reader.stream)
            try {
                r.block()
            } finally {
                r.close()

                val remaining = header.dataSize - r.position
                if (remaining > 0) {
                    reader.stream.discardFully(remaining)
                }

                //
                childrenCache = r.children
                reader.position += header.dataSize
                reader.lastElementRead = true
            }
        }

        override fun toString(): String =
            "Element::Master(header=$header${if (::childrenCache.isInitialized) ", children=$childrenCache" else ""})"
    }

    override suspend fun skipLastChild() {
        lastElement?.let { last ->
            last.skipDataFully(stream)
            position += last.dataSize
            lastElementRead = true
        }
    }

    override fun self(): MasterElement = MasterElement.Actual(header, children)

//    companion object {
//        private val log by logging { }
//    }
}