package saisei.io.format.ebml.element

public interface StringElement : Element {
    public val unicode: Boolean

    public suspend fun read(): String

    public data class Actual(
        override val header: ElementHeader,
        override val unicode: Boolean,
        val value: String,
    ) : StringElement {
        override suspend fun read(): String = value

        override fun toString(): String = "Element::String(header=$header, utf8=$unicode, value=$value)"
    }
}
