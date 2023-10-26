package saisei.io.format.ebml.element

public interface CustomElement<T> : Element {
    public suspend fun read(): T
}