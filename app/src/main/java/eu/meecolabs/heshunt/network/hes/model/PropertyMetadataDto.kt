package eu.meecolabs.heshunt.network.hes.model

import kotlinx.serialization.Serializable

@Serializable
data class PropertyMetadataDto(
    val PIC_ID: String,
    val PIC_NAME: String,
    val LINK: String,
    val LOCAL_AUTH: String
)
