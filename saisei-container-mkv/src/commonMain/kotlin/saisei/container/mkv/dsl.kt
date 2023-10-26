package saisei.container.mkv

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

internal fun ticksToDuration(scale: Long, ticks: Long): Duration = ticksToDuration(scale, ticks.toDouble())
internal fun ticksToDuration(scale: Long, ticks: Float): Duration = ticksToDuration(scale, ticks.toDouble())
internal fun ticksToDuration(scale: Long, ticks: Double): Duration = scale.nanoseconds * ticks
