/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.localcards.models

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val siteIds: List<String>,
    val availability: List<AvailabilityDto>? = null
)
