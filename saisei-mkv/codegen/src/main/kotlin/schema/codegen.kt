package schema

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.codegen.kotlinpoet.*
import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.element.ElementDeclaration
import schema.Schema.Element.Value.Type.*

val EBML_ID = typeNameOf<EbmlID>()

fun Schema.Element.getIdCode(): CodeBlock = CodeBlock {
    val nums = common.id
        .removePrefix("0x")
        .chunked(2)
        .joinToString(", ") { "0x$it" }

    add("%L(%S, $nums)", EBML_ID, common.name)
}

fun Schema.Element.addDocumentation(to: Documentable.Builder<*>) = addons
    .filterIsInstance<Schema.Element.Addon.Documentation>()
    .addDocumentation(to)

fun List<Schema.Element.Addon.Documentation>.addDocumentation(to: Documentable.Builder<*>) {
    this.sortedBy { it.purpose }
        .forEach { to.addKdoc("**${it.purpose.display}**\n${it.value}") }
}

val Schema.Element.Value.typeName: TypeName
    get() = when (type) {
        uinteger, integer -> typeNameOf<ElementDeclaration.INTEGER>()
        string, utf8 -> typeNameOf<ElementDeclaration.STRING>()
        binary -> typeNameOf<ElementDeclaration.BINARY>()
        float -> typeNameOf<ElementDeclaration.FLOAT>()
        date -> typeNameOf<ElementDeclaration.DATE>()
    }

fun Schema.Element.Value.toCode(): CodeBlock = when (type) {
    integer, uinteger ->
        CodeBlock.of(
            "%L.Actual(%L, %L.${if (type == integer) "Signed" else "Unsigned"})",
            typeName,
            getIdCode(),
            typeNameOf<EBMLIntegerType>()
        )

    binary, float, date -> CodeBlock.of("%L.Actual(%L)", typeName, getIdCode())

    string, utf8 -> CodeBlock.of("%L.Actual(%L, %L)", typeName, getIdCode(), type == utf8)
}

fun Schema.Element.Master.createType(ctx: CodeGenContext): TypeSpec = TypeSpec.`object`(common.name) {
    addDocumentation(this)

    // inherit the Master element declaration
    superclass<ElementDeclaration.MASTER.Actual>(getIdCode())

    // handle each child element.
    val initializer = CodeBlock.builder()
    for (element in elements) {
        // generate the child element's declaration.
        when (element) {
            is Schema.Element.Master ->
                addType(element.createType(ctx))

            is Schema.Element.Value -> {
                val restriction = element.addons
                    .filterIsInstance<Schema.Element.Addon.Restriction>()
                    .firstOrNull()

                if (element.type.isInteger && restriction?.enum?.isNotEmpty() == true) addEnum(element.common.name) {
                    element.addDocumentation(this)

                    addProperty<Long>("code") {
                        initializer("code")
                    }

                    primaryConstructor {
                        addParameter<Long>("code")
                    }

                    for (member in restriction.enum) {
                        val label = member.label
                            .split(' ')
                            .map { it.replace("""[/.)(]""".toRegex(), " ") }
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .joinToString(" ")

                        addEnumConstant(label, TypeSpec.anonymousClass {
                            if (label != member.label) {
                                addKdoc("`${member.label}`${if (member.documentation.isNotEmpty()) "\n\n" else ""}")
                            }

                            member.documentation.addDocumentation(this)
                            addSuperclassConstructorParameter(member.value)
                        })
                    }

                    addCompanionObject("Declaration") {
                        val self = ClassName("", element.common.name)
                        addSuperinterface(ElementDeclaration.CUSTOM::class.asTypeName().parameterizedBy(self))

                        addProperty<EbmlID>("id") {
                            addModifiers(KModifier.OVERRIDE)
                            initializer(element.getIdCode())
                        }

                        addFunction("process") {
                            returns(self)
                            addParameter("data", ClassName("naibu.io.memory", "Memory"))
                            addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)

                            addCode {
                                addStatement(
                                    "val code = data.readEBMLInteger(%L.${if (element.type == uinteger) "Unsigned" else "Signed"})",
                                    typeNameOf<EBMLIntegerType>()
                                )

                                addStatement("return entries.first { it.code == code }")
                            }
                        }
                    }
                } else {
                    val override = OVERRIDE_MAP[element.common.path]

                    //
                    val typeName = override?.type?.invoke(ctx, element)
                        ?: element.typeName

                    val code = override?.initializer?.invoke(ctx, element, typeName, element.getIdCode())
                        ?: element.toCode()

                    addProperty(element.common.name, typeName) {
                        element.addDocumentation(this)
                        initializer(code)
                    }
                }
            }
        }

        // add the child element to this master element.
        initializer.addStatement("add(${element.common.name})")
    }

    addInitializerBlock(initializer.build())
}

inline fun <reified S> TypeSpec.Builder.superclass(vararg params: CodeBlock): TypeSpec.Builder {
    superclass(typeNameOf<S>())
    for (param in params) addSuperclassConstructorParameter(param)
    return this
}
