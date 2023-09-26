package saisei.io.format.ebml

import naibu.ext.into
import naibu.io.memory.DefaultAllocator
import naibu.io.memory.Memory
import saisei.io.format.ebml.element.EBMLException
import saisei.io.format.ebml.element.Element
import saisei.io.format.ebml.element.ElementDeclaration
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline infix fun <reified T : Element> Element.into(declaration: ElementDeclaration<T>): T? {
    return if (this matches declaration) into<T>() else null
}

@OptIn(ExperimentalContracts::class)
inline infix fun <reified T : Element> Element.matches(declaration: ElementDeclaration<T>): Boolean {
    contract {
        returns(true) implies (this@matches is T)
    }

    return header.id matches declaration.id
}

inline fun <reified T : Element> List<Element>.find(declaration: ElementDeclaration<T>): T =
    find { it matches declaration }.into()

@OptIn(ExperimentalContracts::class)
inline infix fun <reified T : Element> Element.mustBe(declaration: ElementDeclaration<T>) {
    contract {
        returns() implies (this@mustBe is T)
    }

    if (this matches declaration) {
        return
    }

    throw EBMLException.UnexpectedElement(header.id, declaration.id)
}
