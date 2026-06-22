package eu.meecolabs.heshunt.model

data class Property(
    val id: String,
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val website: String,
    val localAuthority: String
)
