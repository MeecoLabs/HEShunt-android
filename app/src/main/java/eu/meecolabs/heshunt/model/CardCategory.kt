/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.model

import eu.meecolabs.heshunt.data.localcards.models.Category

enum class CardCategory {
    MAIN,
    RARE,
    SEASONAL
}

fun Category.toModel(): CardCategory =
    when (this) {
        Category.MAIN -> CardCategory.MAIN
        Category.RARE -> CardCategory.RARE
        Category.SEASONAL -> CardCategory.SEASONAL
    }
