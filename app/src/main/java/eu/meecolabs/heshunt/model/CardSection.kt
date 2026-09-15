/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.model

import androidx.annotation.StringRes

data class CardSection(
    @get:StringRes val titleRes: Int,
    val cards: List<CardWithStatus>
)
