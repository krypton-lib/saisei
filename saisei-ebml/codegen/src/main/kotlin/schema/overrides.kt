package schema

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.typeNameOf
import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.element.ElementDeclaration

data class Override(
    val initializer: (CodeGenContext.(element: Schema.Element.Value, type: TypeName, id: CodeBlock) -> CodeBlock)? = null,
    val type: (CodeGenContext.(Schema.Element.Value) -> TypeName)? = null,
)

private val SEEK_ID_ACTUAL_TYPENAME = createCustomElementActualDeclaration<EbmlID>()
private val SEEK_ID_TYPENAME = createCustomElementDeclaration<EbmlID>()

val OVERRIDE_MAP = mapOf(
    "\\Segment\\SeekHead\\Seek\\SeekID" to Override(
        { _, _, id -> CodeBlock.of("""%L(%L, { %L.Binary(it, "") })""", SEEK_ID_ACTUAL_TYPENAME, id, EBML_ID) },
        { SEEK_ID_TYPENAME }
    )
)

inline fun <reified T> createCustomElementDeclaration(): TypeName =
    typeNameOf<ElementDeclaration.CUSTOM<T>>()

inline fun <reified T> createCustomElementActualDeclaration(): TypeName =
    typeNameOf<ElementDeclaration.CUSTOM.Actual<T>>()


