package eu.meecolabs.heshunt.data.hes.models

import kotlinx.serialization.Serializable

@Serializable
data class GeometryDto(
    val coordinates: List<Double>
)
