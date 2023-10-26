package saisei.io.format.ebml.element

public interface FloatElement : Element {
    public suspend fun read(): Double

    public data class Actual(override val header: ElementHeader, val value: Double) : FloatElement {
        override suspend fun read(): Double = value

        override fun toString(): String = "Element::Float(header=$header, value=$value)"
    }
}
