package schema

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import java.io.InputStream
import kotlin.collections.set
import org.jsoup.nodes.Element as XmlElement

data class PreElement(val id: String, val path: String, val name: String, val type: String, val xml: XmlElement) {
    val levels: List<String> = path
        .split('\\')
        .drop(1)
        .dropLast(1)

    val level = levels.size

    override fun toString(): String = "PreElement(level=$level, id=$id, path=$path, name=$name, type=$type)"
}

fun XmlElement.toPreElement(): PreElement {
    require(tag().name == "element") {
        "tag is not 'element'"
    }

    val id = attr("id")
    val type = attr("type")
    val path = attr("path")
    val name = attr("name")

    return PreElement(id, path, name, type, this)
}

sealed interface OrganizedPreElement {
    val id: String
    val name: String
    val path: String
    val xml: XmlElement

    data class Master(
        override val id: String,
        override val name: String,
        override val path: String,
        override val xml: XmlElement,
        val values: MutableList<Value>,
        val nestedLevels: MutableMap<String, Master>,
    ) : OrganizedPreElement

    data class Value(
        override val id: String,
        override val name: String,
        override val path: String,
        override val xml: XmlElement,
    ) : OrganizedPreElement
}

fun PreElement.convertToOrganizedMaster() = OrganizedPreElement.Master(
    id,
    name,
    path,
    xml,
    mutableListOf(),
    mutableMapOf()
)

fun PreElement.convertToOrganizedValue() = OrganizedPreElement.Value(id, name, path, xml)

fun List<PreElement>.organize(): OrganizedPreElement.Master {
    val masters = filter { it.type == "master" }

    val levels = mutableMapOf<String, OrganizedPreElement.Master>()
    for (master in masters.sortedBy { it.level }) {
        if (master.level == 0) {
            levels[master.name] = master.convertToOrganizedMaster()
            continue
        }

        master.path.split('\\')
            .drop(2)
            .map { it.removePrefix("+") }
            .fold(levels[master.levels.first()]!!) { acc, level ->
                acc.nestedLevels.computeIfAbsent(level) { _ ->
                    masters.find { it.name == level }!!.convertToOrganizedMaster()
                }
            }
    }

    for (el in filterNot { it.type == "master" }.sortedByDescending { it.level }) {
        el.levels
            .map { it.removePrefix("+") }
            .fold<String, OrganizedPreElement.Master?>(null) { acc, parentName ->
                acc?.nestedLevels?.get(parentName) ?: levels[parentName]
            }
            ?.values
            ?.add(el.convertToOrganizedValue())
    }

    return levels.entries.first().value
}

fun OrganizedPreElement.Master.format(level: Int = 0): String {
    val indent = "\t".repeat(level)
    return buildString {
        appendLine("$indent$name -> (")
        append(values.joinToString("\n") { "$indent\t${it.name}," })
        appendLine()
        append(nestedLevels.format(level + 1))
        appendLine()
        append("$indent)")
    }
}

fun MutableMap<String, OrganizedPreElement.Master>.format(level: Int): String =
    values.joinToString("\n") { it.format(level) }

fun OrganizedPreElement.extractCommonAttributes(): Schema.Element.CommonAttributes = Schema.Element.CommonAttributes(
    id,
    name,
    path,
    xml.attr("maxOccurs").toIntOrNull(),
    xml.attr("minOccurs").toIntOrNull()
)

fun OrganizedPreElement.extractAddons(): List<Schema.Element.Addon> = buildList {
    fun Element.readDocumentation() = Schema.Element.Addon.Documentation(
        attr("lang").takeUnless { it.isBlank() },
        Schema.Element.Addon.Documentation.Purpose.fromRaw(attr("purpose")),
        text()
    )

    for (child in xml.children()) {
        val addon = when (child.tagName()) {
            "restriction" -> {
                val enum = child.children()
                    .filter { el -> el.tagName() == "enum" }
                    .map { el ->
                        val docs = el.children()
                            .filter { c -> c.tagName() == "documentation" }
                            .map { it.readDocumentation() }

                        Schema.Element.Addon.Restriction.Enum(el.attr("label"), el.attr("value"), docs)
                    }

                Schema.Element.Addon.Restriction(enum)
            }

            "documentation" -> child.readDocumentation()

            "implementation_note" -> Schema.Element.Addon.ImplementationNote(
                child.attr("note_attribute"),
                child.text()
            )

            else -> null
        }

        if (addon != null) {
            add(addon)
        }
    }
}

fun OrganizedPreElement.toSchemaElement(): Schema.Element = when (this) {
    is OrganizedPreElement.Master -> {
        val children = values
            .map { it.toSchemaElement() } + nestedLevels.values
            .map { it.toSchemaElement() }

        Schema.Element.Master(extractCommonAttributes(), extractAddons(), children)
    }

    is OrganizedPreElement.Value -> Schema.Element.Value(
        extractCommonAttributes(),
        extractAddons(),
        Schema.Element.Value.Type.valueOf(xml.attr("type").replace("-", ""))
    )
}

fun parseEBMLSchema(stream: InputStream): Schema? {
    val document = Jsoup.parse(stream, "utf8", "", Parser.xmlParser())
        .firstElementChild()
        ?: return null

    val root = document
        .children()
        .filter { el -> el.attr("maxOccurs") != "0" && el.attr("minOccurs") != "0" }
        .map { it.toPreElement() }
        .organize()
        .toSchemaElement() as Schema.Element.Master

    return Schema(document.attr("docType"), document.attr("version"), root)
}

fun main() {
    val root = file::class.java.classLoader.getResourceAsStream("schema.xml")
        ?.let(::parseEBMLSchema)
        ?: error("Unable to get MKV EBML schema")

    println(buildString {
        root.toFile("saisei.container.mkv", "elements")
            .writeTo(this)
    })
}
