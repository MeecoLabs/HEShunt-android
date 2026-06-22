package eu.meecolabs.heshunt.ui.screens.cards.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.ui.screens.cards.CardsUiState
import eu.meecolabs.heshunt.ui.screens.cards.list.components.CardListItem
import eu.meecolabs.heshunt.ui.screens.cards.list.components.SectionHeader

@Composable
internal fun CardListContent(
    state: CardsUiState.Success,
    onCardClick: (String) -> Unit,
    onToggleCollected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (state.available.isNotEmpty()) {
            stickyHeader {
                SectionHeader(title = stringResource(R.string.section_available))
            }

            items(state.available, key = { it.id }) { card ->
                CardListItem(
                    card,
                    onClick = { onCardClick(card.id) },
                    onToggle = { onToggleCollected(card.id, true) }
                )
            }
        }

        if (state.upcoming.isNotEmpty()) {
            stickyHeader {
                SectionHeader(title = stringResource(R.string.section_upcoming))
            }

            items(state.upcoming, key = { it.id }) { card ->
                CardListItem(
                    card,
                    onClick = { onCardClick(card.id) },
                    onToggle = { onToggleCollected(card.id, true) }
                )
            }
        }
        if (state.collected.isNotEmpty()) {
            stickyHeader {
                SectionHeader(title = stringResource(R.string.section_collected))
            }

            items(state.collected, key = { it.id }) { card ->
                CardListItem(
                    card,
                    onClick = { onCardClick(card.id) },
                    onToggle = { onToggleCollected(card.id, false) }
                )
            }
        }
        if (state.expired.isNotEmpty()) {
            stickyHeader {
                SectionHeader(title = stringResource(R.string.section_expired))
            }

            items(state.expired, key = { it.id }) { card ->
                CardListItem(
                    card,
                    onClick = { onCardClick(card.id) },
                    onToggle = { onToggleCollected(card.id, true) }
                )
            }
        }
    }
}
