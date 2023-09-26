package saisei.io.format.ebml.element

import saisei.io.format.ebml.EBMLIntegerType

interface IntegerElement : Element {
    val type: EBMLIntegerType

    suspend fun read(): Long

    data class Actual(
        override val header: ElementHeader,
        override val type: EBMLIntegerType,
        val value: Long,
    ) : IntegerElement {
        override suspend fun read(): Long = value

        override fun toString(): String = "Element::Integer(header=$header, type=$type, value=$value)"
    }
}