/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collected_cards")
data class CollectedCardEntity(
    @PrimaryKey
    val cardId: String,
    val collectedAt: Long
)
