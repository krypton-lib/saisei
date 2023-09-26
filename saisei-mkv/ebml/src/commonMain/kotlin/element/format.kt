package saisei.io.format.ebml.element

import naibu.io.exception.EOFException
import naibu.io.memory.get
import saisei.io.format.ebml.matches


inline fun Element.formatHeader(block: StringBuilder.() -> Unit): String = buildString {
    val name: String = header.id.name
    return buildString {
        append("<$name>")
        block()
        append("</$name>")
    }
}

@OptIn(ExperimentalStdlibApi::class)
suspend fun Element.format(vararg ignore: ElementDeclaration<*>): String = when (this) {
    is MasterElement -> formatHeader {
        consume {
            while (true) {
                val child = try {
                    readNextChild() ?: continue
                } catch (ex: EOFException) {
                    break
                }

                if (ignore.any { child matches it }) {
                    skipLastChild()
                    continue
                }

                append(child.format(*ignore))
            }
        }
    }

    is BinaryElement -> formatHeader {
        val data = read()
        for (i in data.indices) {
            val hex = data[i].toHexString()
            append("0x$hex${if (i == data.indices.last) "" else " "}")
        }
    }

    is CustomElement<*> -> formatHeader { append(read()) }
    is IntegerElement -> formatHeader { append(read()) }
    is StringElement -> formatHeader { append(read()) }
    is FloatElement -> formatHeader { append(read()) }
    is DateElement -> formatHeader { append(read()) }
}