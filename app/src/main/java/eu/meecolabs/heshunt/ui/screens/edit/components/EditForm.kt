/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.edit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.ui.components.CardCategoryBadge
import eu.meecolabs.heshunt.util.dateLongFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditForm(
    card: Card,
    date: LocalDate,
    onSetDate: (LocalDate) -> Unit,
    property: Property?,
    availableAt: List<Property>,
    allSites: List<Property>,
    onSetProperty: (Property?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateState = remember(date) { TextFieldState(date.format(dateLongFormatter)) }
    val propertyState = remember(property) { TextFieldState(property?.name ?: "") }

    var showDateSelector by rememberSaveable { mutableStateOf(false) }
    var showPropertySelector by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.extraSmall)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )

                CardCategoryBadge(card.category)
            }

            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedTextField(
            state = dateState,
            label = {
                Text(
                    text = stringResource(R.string.edit_collected_on_label)
                )
            },
            labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
            placeholder = {
                Text(
                    text = stringResource(R.string.edit_collected_on_placeholder)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = stringResource(R.string.edit_collected_on_cd)
                )
            },
            enabled = true,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(date) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDateSelector = true
                        }
                    }
                }
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                state = propertyState,
                label = {
                    Text(
                        text = stringResource(R.string.edit_collected_at_label)
                    )
                },
                labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_collected_at_placeholder)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_dropdown),
                        contentDescription = stringResource(R.string.edit_collected_at_cd)
                    )
                },
                enabled = true,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(property) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                showPropertySelector = true
                            }
                        }
                    }
            )

            DropdownMenu(
                expanded = showPropertySelector,
                onDismissRequest = {
                    showPropertySelector = false
                }
            ) {
                if (property != null) {
                    DropdownMenuItem(
                        onClick = {
                            onSetProperty(null)
                            showPropertySelector = false
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.edit_collect_unset_property)
                            )
                        }
                    )
                }

                Text(
                    text = stringResource(R.string.edit_collect_property_available_title),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                availableAt.forEach { site ->
                    DropdownMenuItem(
                        onClick = {
                            onSetProperty(site)
                            showPropertySelector = false
                        },
                        text = {
                            Text(
                                text = site.name
                            )
                        },
                        trailingIcon = {
                            if (property == site) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_checkmark),
                                    contentDescription = stringResource(R.string.selected_property)
                                )
                            }
                        }
                    )
                }

                Text(
                    text = stringResource(R.string.edit_collect_other_sites_title),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                allSites.forEach { site ->
                    DropdownMenuItem(
                        onClick = {
                            onSetProperty(site)
                            showPropertySelector = false
                        },
                        text = {
                            Text(
                                text = site.name
                            )
                        },
                        trailingIcon = {
                            if (property == site) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_checkmark),
                                    contentDescription = stringResource(R.string.selected_property)
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDateSelector) {
        DatePickerModal(
            initialDate = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            onDateSelected = { millis ->
                onSetDate(millis.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() })
            },
            onDismiss = {
                showDateSelector = false
            }
        )
    }
}
