package eu.meecolabs.heshunt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.Property
import java.time.LocalDate

@Composable
internal fun PropertyPopup(
    property: Property,
    cards: List<Card>?,
    onDismiss: () -> Unit,
    onCardClick: (Card) -> Unit,
    onWebsiteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(eu.meecolabs.heshunt.R.drawable.ic_close),
                        contentDescription = stringResource(eu.meecolabs.heshunt.R.string.property_close),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = property.localAuthority,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (!cards.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(eu.meecolabs.heshunt.R.string.property_available_cards),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                val now = LocalDate.now()
                cards.forEach { card ->
                    CardStatusRow(
                        card = card,
                        now = now,
                        onClick = { onCardClick(card) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onWebsiteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(eu.meecolabs.heshunt.R.string.property_open_website))
            }
        }
    }
}

@Composable
private fun CardStatusRow(
    card: Card,
    now: LocalDate,
    onClick: () -> Unit
) {
    val (icon, statusText) = when {
        card.isCollected ->
            "✅" to stringResource(eu.meecolabs.heshunt.R.string.status_collected)

        else ->
            when (card.getStatus(now)) {
                CardStatus.ACTIVE -> "⭕" to stringResource(eu.meecolabs.heshunt.R.string.status_missing)
                CardStatus.UPCOMING -> "⏳" to stringResource(eu.meecolabs.heshunt.R.string.status_upcoming)
                CardStatus.EXPIRED -> "❌" to stringResource(eu.meecolabs.heshunt.R.string.status_expired)
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
                text = card.name,
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
