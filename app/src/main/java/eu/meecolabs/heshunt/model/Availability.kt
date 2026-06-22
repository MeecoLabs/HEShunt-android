package eu.meecolabs.heshunt.model

import java.time.LocalDate

data class Availability(
    val from: LocalDate,
    val until: LocalDate,
    val siteIds: List<String>? = null
)
