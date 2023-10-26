package saisei.io.slice.cursor

import naibu.math.toIntSafe
import saisei.io.charset.Charset
import saisei.io.charset.Charsets
import saisei.io.charset.decodeIntoString
import saisei.io.memory.ByteMemory
import saisei.io.slice.ByteSlice
import saisei.io.slice.impl.get
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun ByteSlice.createReadCursor(): ByteReadCursor = ByteReadCursor(this)
public inline fun ByteSlice.createWriteCursor(): ByteWriteCursor = ByteWriteCursor(this)

@OptIn(ExperimentalContracts::class)
public inline fun <T> ByteSlice.read(block: ByteReadCursor.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return block(createReadCursor())
}

@OptIn(ExperimentalContracts::class)
public inline fun ByteSlice.writer(block: ByteWriteCursor.() -> Unit): ByteSlice {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return createWriteCursor()
        .apply(block)
        .view()
}

// TODO: support reading variable-length text

public fun ByteReadCursor.read(n: Int): ByteSlice = read(n.toLong())
public fun ByteReadCursor.read(dst: ByteArray, at: IntRange = dst.indices): Int = read(dst[at]).toIntSafe()
public fun ByteReadCursor.read(dst: ByteMemory, at: LongRange = dst.indices): Long = read(dst[at])
public fun ByteReadCursor.readText(n: Int, charset: Charset = Charsets.UTF8): String = read(n).decodeIntoString(charset)

public fun ByteWriteCursor.write(src: ByteArray, from: IntRange = src.indices): Int = write(src[from]).toIntSafe()
public fun ByteWriteCursor.write(src: ByteMemory, from: LongRange = src.indices): Long = write(src[from])

public fun ByteWriteCursor.write(
    value: CharSequence,
    range: IntRange = value.indices,
    charset: Charset = Charsets.UTF8,
): Int =
    write(charset.encode(value, range))
