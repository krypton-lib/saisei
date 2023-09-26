package saisei.io.format.ebml.element

interface FloatElement : Element {
    suspend fun read(): Double

    data class Actual(override val header: ElementHeader, val value: Double) : FloatElement {
        override suspend fun read(): Double = value

        override fun toString(): String = "Element::Float(header=$header, value=$value)"
    }
}
