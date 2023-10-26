package schema

import dev.kord.codegen.kotlinpoet.FileSpec

data class Schema(
    val docType: String,
    val version: String,
    val root: Element.Master,
) {
    fun toFile(packageName: String, fileName: String) = FileSpec(packageName, fileName) {
        addType(root.createType(CodeGenContext(this@Schema)))
    }

    sealed interface Element {
        val common: CommonAttributes

        val addons: List<Addon>

        data class Master(
            override val common: CommonAttributes,
            override val addons: List<Addon>,
            val elements: List<Element>,
        ) : Element

        data class Value(
            override val common: CommonAttributes,
            override val addons: List<Addon>,
            val type: Type,
        ) : Element {
            enum class Type {
                integer,
                uinteger,
                float,
                string,
                date,
                utf8,
                binary;

                val isInteger: Boolean
                    get() = this in listOf(uinteger, integer)
            }
        }

        data class CommonAttributes(
            val id: String,
            val name: String,
            val path: String,
            val maxOccurs: Int?,
            val minOccurs: Int?,
        )

        sealed interface Addon {
            data class Restriction(val enum: List<Enum>) : Addon {
                data class Enum(val label: String, val value: String, val documentation: List<Documentation>)
            }

            data class Documentation(val lang: String?, val purpose: Purpose, val value: String) : Addon {
                enum class Purpose(val display: String) {
                    Definition("Definition"),
                    Rationale("Rationale"),
                    UsageNotes("Usage Notes"),
                    References("References");

                    companion object {
                        fun fromRaw(value: String) = when (value.lowercase()) {
                            "definition" -> Definition
                            "rationale" -> Rationale
                            "usage notes" -> UsageNotes
                            "references" -> References
                            else -> error("unknown purpose: $value")
                        }
                    }
                }
            }

            data class ImplementationNote(val noteAttribute: String, val value: String) : Addon

            data object Extension : Addon
        }
    }
}
