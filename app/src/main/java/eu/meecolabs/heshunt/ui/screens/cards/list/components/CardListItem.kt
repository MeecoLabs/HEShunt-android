/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.cards.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.CardWithStatus
import eu.meecolabs.heshunt.ui.components.CardCategoryBadge
import eu.meecolabs.heshunt.util.dateFormatter
import androidx.compose.material3.Card as MaterialCard

@Composable
internal fun CardListItem(
    item: CardWithStatus,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    MaterialCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.card.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    CardCategoryBadge(item.card.category)
                }

                item.card.collectedAt?.let { collectedAt ->
                    Text(
                        text = stringResource(R.string.card_collected_on, collectedAt.format(dateFormatter)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            TextButton(
                onClick = onToggle
            ) {
                Text(
                    text = if (item.card.isCollected)
                        stringResource(R.string.card_remove_collected)
                    else
                        stringResource(R.string.status_collect)
                )
            }
        }
    }
}
