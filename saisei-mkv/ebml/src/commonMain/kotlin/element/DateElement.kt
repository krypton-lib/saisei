package saisei.io.format.ebml.element

import kotlinx.datetime.Instant

interface DateElement : Element {
    suspend fun read(): Instant
}