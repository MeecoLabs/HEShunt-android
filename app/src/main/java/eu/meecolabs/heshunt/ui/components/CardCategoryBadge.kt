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
    val (containerColor, contentColor) = when (category) {
        CardCategory.RARE -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)

        CardCategory.SEASONAL -> Pair(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)

        CardCategory.MAIN -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Badge(
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Text(
            text = category.name,
            modifier = Modifier.padding(all = 2.dp)
        )
    }
}
