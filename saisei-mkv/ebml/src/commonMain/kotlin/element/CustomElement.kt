package saisei.io.format.ebml.element

interface CustomElement<T> : Element {
    suspend fun read(): T
}