/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.CardWithStatus

@Composable
internal fun CardStatusRow(
    item: CardWithStatus,
    onClick: () -> Unit
) {
    val (icon, statusText) = when {
        item.card.isCollected ->
            "✅" to stringResource(R.string.status_collected)

        else ->
            when (item.status) {
                CardStatus.ACTIVE -> "⭕" to stringResource(R.string.status_missing)
                CardStatus.UPCOMING -> "⏳" to stringResource(R.string.status_upcoming)
                CardStatus.EXPIRED -> "❌" to stringResource(R.string.status_expired)
            }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = icon,
            modifier = Modifier.padding(end = 8.dp)
        )

        Column {
            Text(
                text = item.card.name,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
