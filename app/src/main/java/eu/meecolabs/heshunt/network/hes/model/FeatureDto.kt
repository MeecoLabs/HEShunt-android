package eu.meecolabs.heshunt.network.hes.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureDto(
    val geometry: GeometryDto,
    val properties: PropertyMetadataDto
)
