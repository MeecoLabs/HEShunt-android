/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.cards.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.model.CardSection
import eu.meecolabs.heshunt.ui.screens.cards.list.components.CardListItem
import eu.meecolabs.heshunt.ui.screens.cards.list.components.SectionHeader

@Composable
internal fun CardListContent(
    sections: List<CardSection>,
    onCardClick: (String) -> Unit,
    onToggleCollected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationBars = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp + navigationBars.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        sections.forEach { section ->
            if (section.cards.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(title = stringResource(section.titleRes))
                }

                items(section.cards, key = { it.card.id }) { item ->
                    CardListItem(
                        item,
                        onClick = { onCardClick(item.card.id) },
                        onToggle = { onToggleCollected(item.card.id, !item.card.isCollected) }
                    )
                }
            }
        }
    }
}
