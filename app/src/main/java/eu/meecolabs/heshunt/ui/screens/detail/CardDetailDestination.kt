package eu.meecolabs.heshunt.ui.screens.detail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CardDetailDestination(
    val id: String
) : NavKey
