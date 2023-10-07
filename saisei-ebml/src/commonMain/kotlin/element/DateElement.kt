package saisei.io.format.ebml.element

import kotlinx.datetime.Instant

public interface DateElement : Element {
    public suspend fun read(): Instant
}