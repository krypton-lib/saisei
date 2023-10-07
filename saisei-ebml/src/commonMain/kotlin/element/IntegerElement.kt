package saisei.io.format.ebml.element

import saisei.io.format.ebml.EBMLIntegerType

public interface IntegerElement : Element {
    public val type: EBMLIntegerType

    public suspend fun read(): Long

    public data class Actual(
        override val header: ElementHeader,
        override val type: EBMLIntegerType,
        val value: Long,
    ) : IntegerElement {
        override suspend fun read(): Long = value

        override fun toString(): String = "Element::Integer(header=$header, type=$type, value=$value)"
    }
}