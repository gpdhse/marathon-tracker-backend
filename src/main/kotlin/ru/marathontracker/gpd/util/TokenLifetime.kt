package ru.marathontracker.gpd.util

import kotlin.time.Duration.Companion.days

object TokenLifetime {
    val REFRESH: Long = 0x11A.days.inWholeMilliseconds
    val ACCESS: Long = 0x5.days.inWholeMilliseconds
}