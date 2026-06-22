package eu.meecolabs.heshunt.ui.screens.cards.list.components

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
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardCategory
import androidx.compose.material3.Card as MaterialCard

@Composable
internal fun CardListItem(
    card: Card,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    MaterialCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = card.category.name,
                    color = if (card.category == CardCategory.RARE) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TextButton(
                onClick = onToggle
            ) {
                Text(
                    text = if (card.isCollected) stringResource(R.string.status_collected) else stringResource(R.string.status_collect)
                )
            }
        }
    }
}
