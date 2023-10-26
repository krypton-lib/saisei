package saisei.container.mkv

import saisei.container.mkv.element.Segment
import saisei.io.format.ebml.EBML
import saisei.io.format.ebml.element.EBMLException
import saisei.io.format.ebml.element.firstNotNullChildOrNull
import saisei.io.format.ebml.intoOrNull
import saisei.io.stream.SeekableReadStream

public sealed class MatroskaFileException : Exception {
    protected constructor(message: String) : super(message)

    protected constructor(message: String, cause: Throwable?) : super(message, cause)

    public class NotPlayable(message: String) : MatroskaFileException(message)

    public class ReadingFailed(message: String, cause: Throwable? = null) : MatroskaFileException(message, cause)

    public class NotMatroska(message: String = "File is not Matroska") : MatroskaFileException(message)
}

public suspend fun readMatroskaDocType(stream: SeekableReadStream) {
    val docType = try {
        readDocType(stream)
    } catch (ex: EBMLException.UnexpectedElement) {
        if (ex.expected matches EBML.id) {
            // not a matroska file.
            throw MatroskaFileException.NotMatroska("MKV file is missing EBML header element.")
        }

        throw MatroskaFileException.ReadingFailed("Failed to read EBML doc-type")
    }

    if (docType !in listOf("webm", "matroska")) {
        throw MatroskaFileException.NotMatroska("EBML DocType is not webm/matroska")
    }
}

/**
 * Reads a [MatroskaSegment] instance from the given [read stream][stream], it reads up until the first occurrence
 * of a [Segment.Cluster] element ([MatroskaSegment.firstCluster]).
 *
 * @param stream The stream to read the segment from.
 */
public suspend fun readMatroskaSegment(stream: SeekableReadStream): MatroskaSegment = try {
    MatroskaSegment.read(stream)
} catch (ex: Throwable) {
    throw MatroskaFileException.ReadingFailed("Failed to read MKV elements", ex)
}

public suspend fun readMatroskaFile(stream: SeekableReadStream): MatroskaSegment {
    readMatroskaDocType(stream)
    return readMatroskaSegment(stream)
}

public suspend fun readDocType(stream: SeekableReadStream): String? =
    EBML.firstNotNullChildOrNull(stream) { it.intoOrNull(EBML.DocType)?.read() }
