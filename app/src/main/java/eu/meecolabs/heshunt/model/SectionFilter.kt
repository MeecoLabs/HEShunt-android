/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.model

import androidx.annotation.StringRes
import eu.meecolabs.heshunt.R

enum class SectionFilter(
    @get:StringRes val titleRes: Int,
    val filter: (card: CardWithStatus) -> Boolean
) {
    AVAILABLE(
        R.string.section_available,
        filter = { (card, status) ->
            !card.isCollected && status == CardStatus.ACTIVE
        }
    ),
    UPCOMING(
        R.string.section_upcoming,
        filter = { (card, status) ->
            !card.isCollected && status == CardStatus.UPCOMING
        }
    ),
    COLLECTED(
        R.string.section_collected,
        filter = { (card, _) ->
            card.isCollected
        }
    ),
    EXPIRED(
        R.string.section_expired,
        filter = { (card, status) ->
            !card.isCollected && status == CardStatus.EXPIRED
        }
    )
}
