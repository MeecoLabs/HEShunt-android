/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.detail.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.CardWithStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.ui.components.CardCategoryBadge
import eu.meecolabs.heshunt.ui.components.PropertyPopup
import eu.meecolabs.heshunt.util.dateFormatter
import java.time.format.DateTimeFormatter

@Composable
internal fun CardDetailContent(
    item: CardWithStatus,
    availableAt: List<Property>,
    allSites: List<Property>,
    collectedAt: Property?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    var showMap by remember { mutableStateOf(true) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.card.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    CardCategoryBadge(item.card.category)
                }

                if (item.card.availability.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.card_availability_periods),
                        style = MaterialTheme.typography.titleSmall
                    )

                    item.card.availability.forEach { period ->
                        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
                        Text(
                            text = "• %s - %s".format(period.from.format(formatter), period.until.format(formatter)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item.card.collectedOn?.let { collectedOn ->
                    Spacer(modifier = Modifier.height(16.dp))

                    val text = collectedAt?.let { property ->
                        stringResource(
                            R.string.card_collected_on_from,
                            collectedOn.format(dateFormatter),
                            property.name
                        )
                    } ?: stringResource(R.string.card_collected_on, collectedOn.format(dateFormatter))
                    Text(
                        text = text,
                        fontWeight = FontWeight.Light
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
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
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
                            .navigationBarsPadding()
                            .padding(16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp + navInsets.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
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

                                if (!isCurrentlyAvailable && (item.status == CardStatus.ACTIVE)) {
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
