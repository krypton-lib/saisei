package saisei.io.format.ebml.element

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import naibu.cio.stream.read.SeekableReadStream
import naibu.common.use
import naibu.io.exception.EOFException
import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.intoOrNull
import saisei.io.format.ebml.matches

/**
 * This is an experimental Saisei API, beware of bugs!
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalSaiseiApi

@ExperimentalSaiseiApi
suspend fun ElementDeclaration.MASTER.reader(stream: SeekableReadStream): MasterElementReader {
    val header = ElementHeader.read(stream)
    if (header.id matches id) return reader(stream, header)
    throw EBMLException.UnexpectedElement(header.id, id)
}

@ExperimentalSaiseiApi
fun ElementDeclaration.MASTER.reader(stream: SeekableReadStream, header: ElementHeader): MasterElementReader =
    MasterElementDeclarationReader(this, header, stream)


@ExperimentalSaiseiApi
inline fun <reified T : Element> Flow<Element>.filterIsElement(declaration: ElementDeclaration<T>) = this
    .filter { it matches declaration }
    .mapNotNull { it.intoOrNull(declaration) }

@ExperimentalSaiseiApi
suspend fun MasterElement.consumeAsFlow(): Flow<Element> = flow {
    consume {
        readUntilEOF { emit(it); true }
    }
}

sealed class EBMLException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)

    class UnexpectedElement(val received: EbmlID?, val expected: EbmlID) : EBMLException(
        "Expected EBML ID $expected but received $received instead."
    )
}

suspend fun MasterElementReader.readFully() {
    readUntilEOF { child ->
        when (child) {
            is CustomElement<*> -> child.read()
            is IntegerElement -> child.read()
            is BinaryElement -> child.read()
            is StringElement -> child.read()
            is MasterElement -> child.consume { readFully() }
            is FloatElement -> child.read()
            is DateElement -> child.read()
        }

        true
    }
}

suspend fun MasterElement.consumeFully(): MasterElement {
    consume { readFully() }
    return this
}

/**
 * Consumes the entirety of this Master Element.
 * **Warning:** this will read EVERY element.
 *
 * @param stream The read stream to read the element from.
 */
suspend fun ElementDeclaration.MASTER.consumeFully(stream: SeekableReadStream): MasterElement = consume(stream) {
    readFully()
    self()
}

suspend inline fun MasterElementReader.readUntilEOF(block: (Element) -> Boolean) {
    while (true) {
        val child = try {
            readNextChild() ?: continue
        } catch (ex: EOFException) {
            break
        }

        if (!block(child)) break
    }
}

suspend fun <T> ElementDeclaration.MASTER.firstNotNullChildOrNull(
    stream: SeekableReadStream,
    block: suspend (Element) -> T?,
): T? {
    consume(stream) {
        while (true) {
            val child = try {
                readNextChild() ?: continue
            } catch (ex: EOFException) {
                return null
            }

            return block(child) ?: continue
        }
    }

    return null
}

suspend inline fun <T> ElementDeclaration.MASTER.consume(
    stream: SeekableReadStream,
    discardRemaining: Boolean = true,
    block: suspend MasterElementReader.() -> T,
): T {
    val header = ElementHeader.read(stream)
    if (header.id matches id) return consume(stream, header.copy(id = id), discardRemaining, block)
    throw EBMLException.UnexpectedElement(header.id, id)
}

@OptIn(ExperimentalSaiseiApi::class)
suspend inline fun <T> ElementDeclaration.MASTER.consume(
    stream: SeekableReadStream,
    header: ElementHeader,
    discardRemaining: Boolean = true,
    block: suspend MasterElementReader.() -> T,
): T = reader(stream, header).use { reader ->
    try {
        block(reader)
    } finally {
        if (discardRemaining && reader.remaining > 0) stream.discardFully(reader.remaining)
    }
}

/**
 *
 */
inline fun <reified T : Element> MasterElement.childOrNull(declaration: ElementDeclaration<T>) =
    children.find { it matches declaration }?.intoOrNull(declaration)

/**
 *
 */
inline fun <reified T : Element> MasterElement.child(declaration: ElementDeclaration<T>) =
    requireNotNull(childOrNull(declaration)) { "Unable to find child: ${declaration.id}" }

inline fun <reified T : Element> MasterElement.children(declaration: ElementDeclaration<T>): List<T> =
    children.filter { it matches declaration }.mapNotNull { it.intoOrNull(declaration) }
