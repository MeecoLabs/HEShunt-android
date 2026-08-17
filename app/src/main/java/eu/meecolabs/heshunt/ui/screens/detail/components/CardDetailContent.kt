package eu.meecolabs.heshunt.ui.screens.detail.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.ui.components.PropertyPopup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun CardDetailContent(
    card: Card,
    availableAt: List<Property>,
    allSites: List<Property>,
    onToggleCollected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showMap by remember { mutableStateOf(true) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    val status = remember(card) { card.getStatus(LocalDate.now()) }

    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )

                    StatusBadge(status)
                }

                Text(
                    text = card.category.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (card.availability.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.card_availability_periods),
                        style = MaterialTheme.typography.titleSmall
                    )

                    card.availability.forEach { period ->
                        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
                        Text(
                            text = "• %s - %s".format(period.from.format(formatter), period.until.format(formatter)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onToggleCollected(!card.isCollected) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (card.isCollected) stringResource(R.string.card_mark_missing) else stringResource(R.string.card_mark_collected)
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.card_available_at),
                style = MaterialTheme.typography.titleLarge
            )

            TextButton(onClick = { showMap = !showMap }) {
                Text(
                    text = if (showMap) stringResource(R.string.card_show_list) else stringResource(R.string.card_show_map)
                )
            }
        }

        if (showMap && allSites.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                CardDetailMap(
                    allSites = allSites,
                    availableAt = availableAt,
                    selectedProperty = selectedProperty,
                    onPropertyClick = { selectedProperty = it },
                    modifier = Modifier.fillMaxSize()
                )

                selectedProperty?.let { property ->
                    PropertyPopup(
                        property = property,
                        cards = null,
                        onDismiss = { selectedProperty = null },
                        onWebsiteClick = {
                            val intent = Intent(Intent.ACTION_VIEW, property.website.toUri())
                            context.startActivity(intent)
                        },
                        onCardClick = {
                            // No cards in this view
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(allSites) { property ->
                    val isCurrentlyAvailable = availableAt.any { it.id == property.id }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (isCurrentlyAvailable) {
                            CardDefaults.cardColors()
                        } else {
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = property.localAuthority,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (!isCurrentlyAvailable && (card.getStatus(LocalDate.now()) == CardStatus.ACTIVE)) {
                                    Text(
                                        text = stringResource(R.string.property_not_available),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            OutlinedButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, property.website.toUri())
                                context.startActivity(intent)
                            }) {
                                Text(text = stringResource(R.string.property_website))
                            }
                        }
                    }
                }
            }
        }
    }
}
