/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.model

import java.time.LocalDate

data class Card(
    val id: String,
    val name: String,
    val category: CardCategory,
    val description: String,
    val siteIds: List<String>,
    val collectedAt: LocalDate?,
    val availability: List<Availability> = emptyList()
) {
    val isCollected: Boolean
        get() = collectedAt != null

    fun getStatus(date: LocalDate): CardStatus {
        if (availability.isEmpty()) {
            return CardStatus.ACTIVE
        }
        
        val isCurrentlyAvailable = availability.any { entry ->
            !date.isBefore(entry.from) && !date.isAfter(entry.until)
        }
        if (isCurrentlyAvailable) {
            return CardStatus.ACTIVE
        }

        val isUpcoming = availability.any { entry ->
            date.isBefore(entry.from)
        }
        if (isUpcoming) {
            return CardStatus.UPCOMING
        }

        return CardStatus.EXPIRED
    }

    fun isAssociatedWith(siteId: String): Boolean {
        if (siteIds.contains(siteId)) {
            return true
        }

        return availability.any { it.siteIds?.contains(siteId) == true }
    }

    fun isAvailableAt(siteId: String, date: LocalDate): Boolean {
        if (availability.isEmpty()) {
            return siteIds.contains(siteId)
        }

        return availability.any { entry ->
            val inDateRange = !date.isBefore(entry.from) && !date.isAfter(entry.until)
            if (!inDateRange) {
                return@any false
            }

            val effectiveSiteIds = entry.siteIds
                ?: siteIds
            effectiveSiteIds.contains(siteId)
        }
    }
}
