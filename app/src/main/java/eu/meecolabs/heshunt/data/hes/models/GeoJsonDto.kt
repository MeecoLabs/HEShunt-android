package eu.meecolabs.heshunt.data.hes.models

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonDto(
    val features: List<FeatureDto>
)
