package eu.meecolabs.heshunt.ui.screens.cards

import androidx.annotation.StringRes
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.CardStatus

internal enum class MapFilter(
    @get:StringRes val labelRes: Int,
    val targetStatus: CardStatus? = null
) {
    All(R.string.card_map_filter_all),
    Missing(R.string.card_map_filter_missing, CardStatus.ACTIVE),
    Expired(R.string.card_map_filter_expired, CardStatus.EXPIRED),
    Upcoming(R.string.card_map_filter_upcoming, CardStatus.UPCOMING)
}
