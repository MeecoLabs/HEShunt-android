package eu.meecolabs.heshunt.network.hes.model

import kotlinx.serialization.Serializable

@Serializable
data class GeometryDto(
    val coordinates: List<Double>
)
