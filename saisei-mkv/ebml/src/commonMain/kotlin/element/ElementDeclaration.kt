package saisei.io.format.ebml.element

import naibu.io.memory.Memory
import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.EbmlID
import kotlin.jvm.JvmInline

sealed interface ElementDeclaration<out T : Element> {
    val id: EbmlID

    interface MASTER : ElementDeclaration<MasterElement> {
        val children: List<ElementDeclaration<Element>>

        abstract class Actual(override val id: EbmlID) : MASTER {
            private val mutableChildren = mutableListOf<ElementDeclaration<Element>>()

            override val children: List<ElementDeclaration<Element>> get() = mutableChildren.toList()

            protected fun add(element: ElementDeclaration<Element>) {
                mutableChildren += element
            }

            protected operator fun ElementDeclaration<Element>.unaryPlus() {
                mutableChildren += this
            }

            override fun toString(): String = "Element::Master(id=$id)"
        }
    }

    interface INTEGER : ElementDeclaration<IntegerElement> {
        val type: EBMLIntegerType

        data class Actual(override val id: EbmlID, override val type: EBMLIntegerType) : INTEGER {
            override fun toString(): String = "Element::${type.name}Integer(id=$id)"
        }
    }

    interface STRING : ElementDeclaration<StringElement> {
        val unicode: Boolean

        data class Actual(override val id: EbmlID, override val unicode: Boolean) : STRING {
            override fun toString(): String = "Element::${if (unicode) "Unicode" else ""}String(id=$id)"
        }
    }

    interface DATE : ElementDeclaration<DateElement> {
        @JvmInline
        value class Actual(override val id: EbmlID) : DATE
    }

    interface BINARY : ElementDeclaration<BinaryElement> {
        @JvmInline
        value class Actual(override val id: EbmlID) : BINARY
    }

    interface FLOAT : ElementDeclaration<FloatElement> {
        @JvmInline
        value class Actual(override val id: EbmlID) : FLOAT
    }

    interface CUSTOM<T> : ElementDeclaration<CustomElement<T>> {
        suspend fun process(data: Memory): T

        data class Actual<T>(override val id: EbmlID, val processor: suspend (Memory) -> T) : CUSTOM<T> {
            override suspend fun process(data: Memory): T = processor.invoke(data)
        }
    }
}
