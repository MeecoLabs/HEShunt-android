/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.detail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.model.CardStatus

@Composable
internal fun StatusBadge(
    status: CardStatus,
    modifier: Modifier = Modifier
) {
    val containerColor = when (status) {
        CardStatus.ACTIVE -> Color(0xFF4CAF50)
        CardStatus.UPCOMING -> Color(0xFFFF9800)
        CardStatus.EXPIRED -> Color(0xFFF44336)
    }
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = status.name,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
