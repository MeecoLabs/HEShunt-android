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
import androidx.compose.material3.Badge
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
import eu.meecolabs.heshunt.model.CardCategory
import eu.meecolabs.heshunt.model.CardWithStatus
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.material3.Card as MaterialCard

private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

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

                    Badge(
                        containerColor = if (item.card.category == CardCategory.RARE)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (item.card.category == CardCategory.RARE)
                            MaterialTheme.colorScheme.onTertiary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(
                            text = item.card.category.name,
                            modifier = Modifier.padding(all = 2.dp)
                        )
                    }
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
                        stringResource(R.string.status_collected)
                    else
                        stringResource(R.string.status_collect)
                )
            }
        }
    }
}
