/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.ui.screens.detail.components.CardDetailContent
import eu.meecolabs.heshunt.ui.screens.detail.components.StatusBadge
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardId: String,
    onBackClick: () -> Unit,
    viewModel: CardDetailViewModel = koinViewModel { parametersOf(cardId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val state = uiState
                        val titleText = when (state) {
                            is UiState.Loading ->
                                stringResource(R.string.card_details_loading_title)

                            is UiState.Success ->
                                state.card.card.name

                            is UiState.Error ->
                                stringResource(R.string.card_details_error_title)
                        }

                        Text(
                            text = titleText,
                            modifier = Modifier.weight(1f)
                        )

                        if (state is UiState.Success) {
                            StatusBadge(
                                state.card.status,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                },
                actions = {
                    val state = uiState
                    if (state is UiState.Success) {
                        if (state.card.card.isCollected) {
                            IconButton(
                                onClick = { /* TODO */ }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    contentDescription = stringResource(R.string.card_edit_cd)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.toggleCollected(!state.card.card.isCollected) }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (state.card.card.isCollected)
                                        R.drawable.ic_mark_uncollected
                                    else
                                        R.drawable.ic_mark_collected
                                ),
                                contentDescription = stringResource(R.string.card_remove_cd)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding())

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = contentModifier
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                CardDetailContent(
                    item = state.card,
                    availableAt = state.availableAt,
                    allSites = state.allSites,
                    modifier = contentModifier
                )
            }

            is UiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = contentModifier
                ) {
                    Text(text = state.message)
                }
            }
        }
    }
}
