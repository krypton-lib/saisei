import naibu.cio.stream.read.SeekableReadStream
import naibu.monads.isErr
import naibu.monads.unwrapErr
import naibu.monads.unwrapOk
import saisei.container.mkv.MatroskaFile
import saisei.container.mkv.readMatroskaFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.measureTimedValue

suspend fun Path.readMKV(): Pair<MatroskaFile, InputAsSRS> {
    val stream = this
        .toFile()
        .inputStream()
        .let(::InputAsSRS)

    val (res, took) = measureTimedValue {
        readMatroskaFile(stream)
    }

    log.trace { "Parsing matroska file took: $took" }
    if (res.isErr()) {
        val error = res.unwrapErr()
        log.error(error) { "Unable to read matroska file" }

        throw error
    }

    return res.unwrapOk() to stream
}

suspend fun file(name: String): Pair<MatroskaFile, SeekableReadStream> = Path("$name.webm").readMKV()
