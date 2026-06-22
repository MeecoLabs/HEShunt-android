package eu.meecolabs.heshunt.data.hes.models

import kotlinx.serialization.Serializable

@Serializable
data class FeatureDto(
    val geometry: GeometryDto,
    val properties: PropertyMetadataDto
)
