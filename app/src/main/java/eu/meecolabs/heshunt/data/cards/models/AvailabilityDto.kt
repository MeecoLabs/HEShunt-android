package eu.meecolabs.heshunt.data.cards.models

import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityDto(
    val from: String,
    val until: String,
    val siteIds: List<String>? = null
)
