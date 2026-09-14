/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.localcards.models

import kotlinx.serialization.SerialName

enum class Category {
    @SerialName("main")
    MAIN,

    @SerialName("rare")
    RARE,

    @SerialName("seasonal")
    SEASONAL
}
