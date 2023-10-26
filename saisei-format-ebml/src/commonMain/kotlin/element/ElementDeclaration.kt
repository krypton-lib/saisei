package saisei.io.format.ebml.element

import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.EbmlID
import saisei.io.memory.ByteMemory
import kotlin.jvm.JvmInline

public sealed interface ElementDeclaration<out T : Element> {
    public val id: EbmlID

    public interface MASTER : ElementDeclaration<MasterElement> {
        public val children: List<ElementDeclaration<Element>>

        public abstract class Actual(override val id: EbmlID) : MASTER {
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

    public interface INTEGER : ElementDeclaration<IntegerElement> {
        public val type: EBMLIntegerType

        public data class Actual(override val id: EbmlID, override val type: EBMLIntegerType) : INTEGER {
            override fun toString(): String = "Element::${type.name}Integer(id=$id)"
        }
    }

    public interface STRING : ElementDeclaration<StringElement> {
        public val unicode: Boolean

        public data class Actual(override val id: EbmlID, override val unicode: Boolean) : STRING {
            override fun toString(): String = "Element::${if (unicode) "Unicode" else ""}String(id=$id)"
        }
    }

    public interface DATE : ElementDeclaration<DateElement> {
        @JvmInline
        public value class Actual(override val id: EbmlID) : DATE
    }

    public interface BINARY : ElementDeclaration<BinaryElement> {
        @JvmInline
        public value class Actual(override val id: EbmlID) : BINARY
    }

    public interface FLOAT : ElementDeclaration<FloatElement> {
        @JvmInline
        public value class Actual(override val id: EbmlID) : FLOAT
    }

    public interface CUSTOM<T> : ElementDeclaration<CustomElement<T>> {
        public suspend fun process(data: ByteMemory): T

        public data class Actual<T>(override val id: EbmlID, val processor: suspend (ByteMemory) -> T) : CUSTOM<T> {
            override suspend fun process(data: ByteMemory): T = processor.invoke(data)
        }
    }
}
