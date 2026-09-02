/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.model.CardCategory

@Composable
fun CardCategoryBadge(
    category: CardCategory,
    modifier: Modifier = Modifier
) {
    Badge(
        containerColor = if (category == CardCategory.RARE)
            MaterialTheme.colorScheme.tertiary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (category == CardCategory.RARE)
            MaterialTheme.colorScheme.onTertiary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    ) {
        Text(
            text = category.name,
            modifier = Modifier.padding(all = 2.dp)
        )
    }
}
