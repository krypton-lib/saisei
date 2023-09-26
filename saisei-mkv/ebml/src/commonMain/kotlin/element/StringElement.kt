package saisei.io.format.ebml.element

interface StringElement : Element {
    val unicode: Boolean

    suspend fun read(): String

    data class Actual(
        override val header: ElementHeader,
        override val unicode: Boolean,
        val value: String,
    ) : StringElement {
        override suspend fun read(): String = value

        override fun toString(): String = "Element::String(header=$header, utf8=$unicode, value=$value)"
    }
}
