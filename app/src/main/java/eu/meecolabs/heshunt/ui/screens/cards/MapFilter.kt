/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.cards

import androidx.annotation.StringRes
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.CardWithStatus

internal enum class MapFilter(
    @get:StringRes val labelRes: Int,
    val filter: (item: CardWithStatus) -> Boolean
) {
    ALL(R.string.card_map_filter_all, filter = {
        true
    }),
    AVAILABLE_MISSING(R.string.card_map_filter_available_missing, filter = { (card, status) ->
        !card.isCollected && status != CardStatus.EXPIRED
    }),
    UPCOMING_MISSING(R.string.card_map_filter_upcoming_missing, filter = { (card, status) ->
        !card.isCollected && status == CardStatus.UPCOMING
    }),
    EXPIRED_MISSING(R.string.card_map_filter_expired_missing, filter = { (card, status) ->
        !card.isCollected && status == CardStatus.EXPIRED
    }),
    ALL_MISSING(R.string.card_map_filter_all_missing, filter = { (card, _) ->
        !card.isCollected
    }),
    COLLECTED(R.string.card_map_filter_collected, filter = { (card, _) ->
        card.isCollected
    })
}
